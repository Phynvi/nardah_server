package io.battlerune.net.packet.out;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.Hitsplat;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.UpdateFlag;
import io.battlerune.game.world.entity.actor.Viewport;
import io.battlerune.game.world.entity.actor.player.ForceMovement;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.appearance.Gender;
import io.battlerune.game.world.entity.actor.player.relations.ChatMessage;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.containers.equipment.Equipment;
import io.battlerune.game.world.items.containers.equipment.EquipmentType;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.codec.AccessType;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketBuilder;
import io.battlerune.net.packet.PacketType;
import io.battlerune.util.Utility;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

import java.util.Collection;
import java.util.Iterator;

public final class SendPlayerUpdate extends OutgoingPacket {
	
	/**
	 * Logger.
	 */
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	public SendPlayerUpdate() {
		super(81, PacketType.VAR_SHORT);
	}
	
	@Override
	public boolean encode(Player player) {
		if(player.regionChange) {
			player.send(new SendMapRegion());
		}
		
		final PacketBuilder blockBuf = PacketBuilder.alloc();
		builder.initializeAccess(AccessType.BIT);
		
		try {
			updateMovement(player, builder);
			
			if(player.isUpdateRequired()) {
				updatePlayer(blockBuf, player, player, UpdateState.UPDATE_SELF);
			}
			
			final Collection<Player> localPlayers = World.getRegions().getLocalPlayers(player);
			
			builder.writeBits(8, player.viewport.getPlayersInViewport().size());
			
			for(Iterator<Player> itr = player.viewport.getPlayersInViewport().iterator(); itr.hasNext(); ) {
				Player other = itr.next();
				
				if(player.viewport.shouldRemove(other)) {
					builder.writeBit(true);
					builder.writeBits(2, 3);
					itr.remove();
				} else {
					updateMovement(other, builder);
					
					if(other.isUpdateRequired()) {
						updatePlayer(blockBuf, player, other, UpdateState.UPDATE_LOCAL);
					}
				}
				
			}
			
			int added = 0;
			
			for(Player localPlayer : localPlayers) {
				
				if(player.viewport.getPlayersInViewport().size() >= Viewport.CAPACITY || added >= Viewport.ADD_THRESHOLD) {
					break;
				}
				
				if(player.viewport.add(localPlayer)) {
					added++;
					addNewPlayer(builder, player, localPlayer);
					updatePlayer(blockBuf, player, localPlayer, UpdateState.ADD_LOCAL);
				}
				
			}
			
			if(blockBuf.buffer().readableBytes() > 0) {
				builder.writeBits(11, 0x7FF);
				builder.initializeAccess(AccessType.BYTE);
				builder.writeBuffer(blockBuf.buffer());
			} else {
				builder.initializeAccess(AccessType.BYTE);
			}
		} catch(Exception ex) {
			logger.error(String.format("error updating player=%s", player));
			ex.printStackTrace();
		}
		return true;
	}
	
	private static void addNewPlayer(PacketBuilder packetBuf, Player player, Player other) {
		packetBuf.writeBits(11, other.getIndex()).writeBit(true).writeBit(true).writeBits(5, other.getY() - player.getY()).writeBits(5, other.getX() - player.getX());
	}
	
	private static void updatePlayer(PacketBuilder blockBuf, Player player, Player other, UpdateState state) {
		if(!other.isUpdateRequired()) {
			return;
		}
		
		int mask = 0;
		
		if(other.updateFlags.contains(UpdateFlag.FORCE_MOVEMENT) && other.getForceMovement() != null) {
			mask |= 0x400;
		}
		
		if(other.updateFlags.contains(UpdateFlag.GRAPHICS) && other.getGraphic().isPresent()) {
			mask |= 0x100;
		}
		
		if(other.updateFlags.contains(UpdateFlag.ANIMATION) && other.getAnimation().isPresent()) {
			mask |= 0x8;
		}
		
		if(other.updateFlags.contains(UpdateFlag.FORCED_CHAT)) {
			mask |= 0x4;
		}
		
		if(other.updateFlags.contains(UpdateFlag.CHAT) && state != UpdateState.UPDATE_SELF && other.getChatMessage().isPresent()) {
			mask |= 0x80;
		}
		
		if(other.updateFlags.contains(UpdateFlag.INTERACT)) {
			mask |= 0x1;
		}
		
		if(other.updateFlags.contains(UpdateFlag.APPEARANCE)) {
			mask |= 0x10;
		}
		
		if(other.updateFlags.contains(UpdateFlag.FACE_COORDINATE)) {
			mask |= 0x2;
		}
		
		if(other.updateFlags.contains(UpdateFlag.FIRST_HIT)) {
			mask |= 0x20;
		}
		
		if(other.updateFlags.contains(UpdateFlag.SECOND_HIT)) {
			mask |= 0x200;
		}
		
		if(mask >= 0x100) {
			mask |= 0x40;
			blockBuf.writeByte(mask & 0xFF);
			blockBuf.writeByte(mask >> 8);
		} else {
			blockBuf.writeByte(mask);
		}
		
		if(other.updateFlags.contains(UpdateFlag.FORCE_MOVEMENT) && other.getForceMovement() != null) {
			appendForceMovementMask(player, other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.GRAPHICS) && other.getGraphic().isPresent()) {
			appendGraphicMask(other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.ANIMATION) && other.getAnimation().isPresent()) {
			appendAnimationMask(other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.FORCED_CHAT)) {
			appendForceChatMask(other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.CHAT) && state != UpdateState.UPDATE_SELF && other.getChatMessage().isPresent()) {
			appendChatMask(blockBuf, other);
		}
		
		if(other.updateFlags.contains(UpdateFlag.INTERACT)) {
			appendFaceEntityMask(other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.APPEARANCE)) {
			appendAppearanceMask(other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.FACE_COORDINATE)) {
			appendFaceCoordinteMask(other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.FIRST_HIT)) {
			appendHitMask(other, blockBuf);
		}
		
		if(other.updateFlags.contains(UpdateFlag.SECOND_HIT)) {
			appendSecondHitMask(other, blockBuf);
		}
		
	}
	
	private static void appendForceMovementMask(Player player, Player other, PacketBuilder blockBuf) {
		final ForceMovement fm = other.getForceMovement();
		final int startX = other.getPosition().getLocalX(player.lastPosition);
		final int startY = other.getPosition().getLocalY(player.lastPosition);
		final int endX = fm.getEnd().getX();
		final int endY = fm.getEnd().getY();
		
		blockBuf.writeByte(startX, ByteModification.SUB).writeByte(startY, ByteModification.SUB).writeByte(startX + endX, ByteModification.SUB).writeByte(startY + endY, ByteModification.SUB).writeShort(fm.getSpeed(), ByteModification.ADD, ByteOrder.LE).writeShort(fm.getReverseSpeed(), ByteModification.ADD).writeByte(fm.getDirection(), ByteModification.SUB);
	}
	
	private static void appendForceChatMask(Player other, PacketBuilder blockBuf) {
		blockBuf.writeString(other.forceChat);
	}
	
	private static void appendFaceEntityMask(Player other, PacketBuilder blockBuf) {
		Actor actor = other.interactingWith;
		if(actor != null) {
			int index = actor.getIndex();
			if(actor.isPlayer()) {
				index += 32768;
			}
			blockBuf.writeShort(index, ByteOrder.LE);
		} else {
			blockBuf.writeShort(65535, ByteOrder.LE);
		}
	}
	
	private static void appendFaceCoordinteMask(Player other, PacketBuilder blockBuf) {
		Position loc = other.facePosition;
		
		if(loc == null) {
			Position currentPos = other.getPosition();
			Direction currentDir = other.movement.lastDirection;
			blockBuf.writeShort(((currentPos.getX() + currentDir.getDirectionX()) << 1), ByteModification.ADD, ByteOrder.LE).writeShort(((currentPos.getY() + currentDir.getDirectionY()) << 1) + 1, ByteOrder.LE);
		} else {
			blockBuf.writeShort((loc.getX() << 1) + 1, ByteModification.ADD, ByteOrder.LE).writeShort((loc.getY() << 1) + 1, ByteOrder.LE);
		}
	}
	
	private static void appendAnimationMask(Player other, PacketBuilder maskBuf) {
		Animation anim = other.getAnimation().orElse(Animation.RESET);
		maskBuf.writeShort(anim.getId(), ByteOrder.LE);
		maskBuf.writeByte(anim.getDelay(), ByteModification.NEG);
	}
	
	private static void appendGraphicMask(Player other, PacketBuilder blockBuf) {
		Graphic graphic = other.getGraphic().orElse(Graphic.RESET);
		blockBuf.writeShort(graphic.getId(), ByteOrder.LE).writeInt(graphic.getDelay() | graphic.getHeight());
	}
	
	private static void appendChatMask(PacketBuilder blockBuf, Player other) {
		final ChatMessage message = other.getChatMessage().orElse(ChatMessage.create("Cabbage"));
		
		final byte[] encoded = message.getEncoded();
		blockBuf.writeShort(((message.getColor().getCode() & 0xFF) << 8) | (message.getEffect().getCode() & 0xFF), ByteOrder.LE).writeByte(other.right.getCrown()).writeByte(encoded.length, ByteModification.NEG).writeBytesReverse(encoded);
	}
	
	private static void appendAppearanceMask(Player other, PacketBuilder blockBuf) {
		final PacketBuilder tempBuf = PacketBuilder.alloc();
		
		tempBuf.writeByte(other.appearance.getGender().ordinal()).writeByte(other.headIcon).writeByte(other.skulling.getHeadIconType().getCode()).writeByte(other.valueIcon);
		
		if(other.id != -1) {
			tempBuf.writeShort(-1);
			tempBuf.writeShort(other.id);
		} else {
			Item helm = other.equipment.get(Equipment.HEAD_SLOT);
			if(helm != null && helm.getId() > 1) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.HELM_SLOT).getId());
			} else {
				tempBuf.writeByte(0);
			}
			
			if(other.equipment.get(Equipment.CAPE_SLOT) != null) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.CAPE_SLOT).getId());
			} else {
				tempBuf.writeByte(0);
			}
			
			if(other.equipment.get(Equipment.AMULET_SLOT) != null) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.AMULET_SLOT).getId());
			} else {
				tempBuf.writeByte(0);
			}
			
			if(other.equipment.get(Equipment.WEAPON_SLOT) != null) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.WEAPON_SLOT).getId());
			} else {
				tempBuf.writeByte(0);
			}
			
			Item torso = other.equipment.get(Equipment.CHEST_SLOT);
			if(torso != null && torso.getId() > 1) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.CHEST_SLOT).getId());
			} else {
				tempBuf.writeShort(0x100 + other.appearance.getTorso());
			}
			
			if(other.equipment.get(Equipment.SHIELD_SLOT) != null) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.SHIELD_SLOT).getId());
			} else {
				tempBuf.writeByte(0);
			}
			
			if(torso != null && torso.getId() > 1 && torso.getEquipmentType().equals(EquipmentType.BODY)) {
				tempBuf.writeByte(0);
			} else {
				tempBuf.writeShort(0x100 + other.appearance.getArms());
			}
			
			if(other.equipment.get(Equipment.LEGS_SLOT) != null) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.LEGS_SLOT).getId());
			} else {
				tempBuf.writeShort(0x100 + other.appearance.getLegs());
			}
			
			boolean head = true;
			boolean beard = true;
			
			if(helm != null && helm.getId() > 1) {
				EquipmentType type = helm.getEquipmentType();
				head = type.equals(EquipmentType.MASK) || type.equals(EquipmentType.HAT);
				beard = type.equals(EquipmentType.FACE) || type.equals(EquipmentType.HAT);
			}
			
			if(head) {
				tempBuf.writeShort(0x100 + other.appearance.getHead());
			} else {
				tempBuf.writeByte(0);
			}
			
			if(other.equipment.get(Equipment.HANDS_SLOT) != null) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.HANDS_SLOT).getId());
			} else {
				tempBuf.writeShort(0x100 + other.appearance.getHands());
			}
			
			if(other.equipment.get(Equipment.FEET_SLOT) != null) {
				tempBuf.writeShort(0x200 + other.equipment.get(Equipment.FEET_SLOT).getId());
			} else {
				tempBuf.writeShort(0x100 + other.appearance.getFeet());
			}
			
			if(other.appearance.getGender().equals(Gender.MALE)) {
				if(beard) {
					tempBuf.writeShort(0x100 + other.appearance.getBeard());
				} else {
					tempBuf.writeByte(0);
				}
			} else {
				tempBuf.writeByte(0);
			}
		}
		
		tempBuf.writeByte(other.appearance.getHairColor()).writeByte(other.appearance.getTorsoColor()).writeByte(other.appearance.getLegsColor()).writeByte(other.appearance.getFeetColor()).writeByte(other.appearance.getSkinColor()).writeShort(other.actorAnimation.getStand()).writeShort(other.actorAnimation.getTurn()).writeShort(other.actorAnimation.getWalk()).writeShort(other.actorAnimation.getTurn180()).writeShort(other.actorAnimation.getTurn90CW()).writeShort(other.actorAnimation.getTurn90CCW()).writeShort(other.actorAnimation.getRun()).writeLong(Utility.hash(other.getName())).writeString(other.playerTitle.getTitle()).writeInt(other.playerTitle.getColor()).writeString(other.clanChannel == null ? "" : other.clanChannel.getOwner()).writeString(other.clanTag).writeString(other.clanTagColor).writeLong(Double.doubleToLongBits(other.skills.getCombatLevel())).writeByte(other.right.getCrown()).writeShort(0);
		blockBuf.writeByte(tempBuf.buffer().writerIndex(), ByteModification.NEG);
		blockBuf.writeBytes(tempBuf.buffer());
	}
	
	private static void appendHitMask(final Player player, final PacketBuilder blockBuf) {
		Hit hit = player.firstHit;
		int id = hit.getHitsplat().getId();
		int max = player.getMaximumHealth() >= 500 ? 200 : 100;
		int health = player.getCurrentHealth() * max / player.getMaximumHealth();
		if(health > max)
			health = max;
		
		if(hit.getHitsplat() == Hitsplat.NORMAL && hit.getDamage() > 0) {
			id++;
		}
		
		blockBuf.writeByte(hit.getDamage());
		blockBuf.writeByte(id, ByteModification.ADD);
		blockBuf.writeByte(hit.getHitIcon().getId());
		blockBuf.writeByte(health);
		blockBuf.writeByte(max, ByteModification.NEG);
	}
	
	private static void appendSecondHitMask(final Player player, final PacketBuilder blockBuf) {
		Hit hit = player.secondHit;
		int id = hit.getHitsplat().getId();
		int max = player.getMaximumHealth() >= 500 ? 200 : 100;
		int health = player.getCurrentHealth() * max / player.getMaximumHealth();
		if(health > max)
			health = max;
		
		if(hit.getHitsplat() == Hitsplat.NORMAL && hit.getDamage() > 0) {
			id++;
		}
		
		blockBuf.writeByte(hit.getDamage());
		blockBuf.writeByte(id, ByteModification.SUB);
		blockBuf.writeByte(hit.getHitIcon().getId());
		blockBuf.writeByte(health);
		blockBuf.writeByte(max, ByteModification.NEG);
	}
	
	private static void updateMovement(Player player, PacketBuilder packetBuf) {
		final boolean teleported = player.positionChange || player.teleportRegion;
		final boolean updateRequired = player.isUpdateRequired();
		if(teleported) {
			packetBuf.writeBit(true).writeBits(2, 3).writeBits(2, player.getHeight()).writeBits(1, player.regionChange ? 0 : 1).writeBit(updateRequired).writeBits(7, player.getPosition().getLocalY(player.lastPosition)).writeBits(7, player.getPosition().getLocalX(player.lastPosition));
		} else if(player.movement.getRunningDirection() != -1) {
			packetBuf.writeBit(true).writeBits(2, 2).writeBits(3, player.movement.getWalkingDirection()).writeBits(3, player.movement.getRunningDirection()).writeBit(player.isUpdateRequired());
		} else if(player.movement.getWalkingDirection() != -1) {
			packetBuf.writeBit(true).writeBits(2, 1).writeBits(3, player.movement.getWalkingDirection()).writeBit(player.isUpdateRequired());
		} else {
			if(updateRequired) {
				packetBuf.writeBit(true).writeBits(2, 0);
			} else {
				packetBuf.writeBit(false);
			}
		}
	}
	
	private enum UpdateState {
		UPDATE_SELF, UPDATE_LOCAL, ADD_LOCAL
	}
	
}
