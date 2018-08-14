package com.nardah.net.packet.out;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.Viewport;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.Hitsplat;
import com.nardah.game.world.position.Position;
import com.nardah.Config;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.AccessType;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketBuilder;
import com.nardah.net.packet.PacketType;

import java.util.Iterator;

/**
 * The packet that's responsible for updating mobs.
 * @author nshusa
 */
public final class SendNpcUpdate extends OutgoingPacket {
	
	public SendNpcUpdate() {
		super(65, PacketType.VAR_SHORT);
	}
	
	@Override
	public boolean encode(Player player) {
		PacketBuilder maskBuf = PacketBuilder.alloc();
		try {
			builder.initializeAccess(AccessType.BIT);
			builder.writeBits(8, player.viewport.getNpcsInViewport().size());
			
			for(Iterator<Mob> itr = player.viewport.getNpcsInViewport().iterator(); itr.hasNext(); ) {
				
				Mob mob = itr.next();
				
				if(player.viewport.shouldRemove(mob)) {
					if(mob.atomicPlayerCount.decrementAndGet() < 0) {
						mob.atomicPlayerCount.set(0);
					}
					itr.remove();
					builder.writeBits(1, 1);
					builder.writeBits(2, 3);
				} else {
					updateMovement(builder, mob);
					
					if(mob.isUpdateRequired()) {
						updateNpc(maskBuf, mob);
					}
				}
				
			}
			
			int npcsAdded = 0;
			
			for(Mob localMob : World.getRegions().getLocalNpcs(player)) {
				
				if(player.viewport.getNpcsInViewport().size() >= Viewport.CAPACITY || npcsAdded == Viewport.ADD_THRESHOLD) {
					break;
				}
				
				if(player.viewport.add(localMob)) {
					npcsAdded++;
					addNewNpc(builder, player, localMob);
					updateNpc(maskBuf, localMob);
					
					if(localMob.atomicPlayerCount.incrementAndGet() < 0) {
						localMob.atomicPlayerCount.set(0);
					}
				}
				
			}
			
			if(maskBuf.buffer().readableBytes() > 0) {
				builder.writeBits(14, 16383);
				builder.initializeAccess(AccessType.BYTE);
				builder.writeBytes(maskBuf.buffer());
			} else {
				builder.initializeAccess(AccessType.BYTE);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}
	
	private static void addNewNpc(PacketBuilder packet, Player player, Mob mob) {
		packet.writeBits(14, mob.getIndex());
		packet.writeBits(5, mob.getPosition().getY() - player.getPosition().getY());
		packet.writeBits(5, mob.getPosition().getX() - player.getPosition().getX());
		packet.writeBits(1, 0);
		packet.writeBits(Config.NPC_BITS, mob.id);
		packet.writeBits(1, mob.isUpdateRequired() ? 1 : 0);
	}
	
	private static void updateMovement(PacketBuilder packet, Mob mob) {
		final boolean updateRequired = mob.isUpdateRequired();
		if(mob.movement.getRunningDirection() != -1) {
			packet.writeBit(true).writeBits(2, 2).writeBits(3, mob.movement.getWalkingDirection()).writeBits(3, mob.movement.getRunningDirection()).writeBit(mob.isUpdateRequired());
		} else if(mob.movement.getWalkingDirection() != -1) {
			packet.writeBit(true).writeBits(2, 1).writeBits(3, mob.movement.getWalkingDirection()).writeBit(mob.isUpdateRequired());
		} else {
			packet.writeBit(updateRequired);
			if(updateRequired) {
				packet.writeBits(2, 0);
			}
		}
	}
	
	private static void updateNpc(PacketBuilder maskBuf, Mob mob) {
		if(!mob.isUpdateRequired()) {
			return;
		}
		
		int mask = 0;
		
		if(mob.updateFlags.contains(UpdateFlag.ANIMATION) && mob.getAnimation().isPresent()) {
			mask |= 0x10;
		}
		
		if(mob.updateFlags.contains(UpdateFlag.GRAPHICS) && mob.getGraphic().isPresent()) {
			mask |= 0x80;
		}
		
		if(mob.updateFlags.contains(UpdateFlag.INTERACT)) {
			mask |= 0x20;
		}
		
		if(mob.updateFlags.contains(UpdateFlag.FORCED_CHAT)) {
			mask |= 0x1;
		}
		
		if(mob.updateFlags.contains(UpdateFlag.FIRST_HIT)) {
			mask |= 0x40;
		}
		
		if(mob.updateFlags.contains(UpdateFlag.SECOND_HIT)) {
			mask |= 0x8;
		}
		
		if(mob.updateFlags.contains(UpdateFlag.TRANSFORM)) {
			mask |= 0x2;
		}
		
		if(mob.updateFlags.contains(UpdateFlag.FACE_COORDINATE)) {
			mask |= 0x4;
		}
		
		maskBuf.writeByte(mask);
		
		if(mob.updateFlags.contains(UpdateFlag.ANIMATION) && mob.getAnimation().isPresent()) {
			appendAnimationMask(mob, maskBuf);
		}
		
		if(mob.updateFlags.contains(UpdateFlag.GRAPHICS) && mob.getGraphic().isPresent()) {
			appendGfxMask(mob, maskBuf);
		}
		
		if(mob.updateFlags.contains(UpdateFlag.INTERACT)) {
			appendFaceEntityMask(mob, maskBuf);
		}
		
		if(mob.updateFlags.contains(UpdateFlag.FORCED_CHAT)) {
			appendForceChatMask(mob, maskBuf);
		}
		
		if(mob.updateFlags.contains(UpdateFlag.FIRST_HIT)) {
			appendFirstHitMask(mob, maskBuf);
		}
		
		if(mob.updateFlags.contains(UpdateFlag.SECOND_HIT)) {
			appendSecondHitMask(mob, maskBuf);
		}
		
		if(mob.updateFlags.contains(UpdateFlag.TRANSFORM)) {
			appendTransformationMask(mob, maskBuf);
		}
		
		if(mob.updateFlags.contains(UpdateFlag.FACE_COORDINATE)) {
			appendFaceCoordinateMask(mob, maskBuf);
		}
		
	}
	
	private static void appendAnimationMask(Mob mob, PacketBuilder maskBuf) {
		Animation anim = mob.getAnimation().orElse(Animation.RESET);
		maskBuf.writeShort(anim.getId(), ByteOrder.LE).writeByte(anim.getDelay());
	}
	
	private static void appendGfxMask(Mob mob, PacketBuilder maskBuf) {
		Graphic gfx = mob.getGraphic().orElse(Graphic.RESET);
		maskBuf.writeShort(gfx.getId()).writeInt(gfx.getDelay() | gfx.getHeight());
	}
	
	private static void appendFaceEntityMask(Mob mob, PacketBuilder maskBuf) {
		Actor actor = mob.interactingWith;
		int index = 65535;
		if(actor != null) {
			index = actor.getIndex();
			if(actor.isPlayer()) {
				index += 32768;
			}
			maskBuf.writeShort(index);
		} else {
			maskBuf.writeShort(index);
		}
	}
	
	private static void appendForceChatMask(Mob mob, PacketBuilder maskBuf) {
		maskBuf.writeString(mob.forceChat);
	}
	
	private static void appendFaceCoordinateMask(Mob mob, PacketBuilder maskBuf) {
		Position loc = mob.facePosition;
		if(loc == null) {
			Position currentPos = mob.getPosition();
			Direction currentDir = mob.movement.lastDirection;
			maskBuf.writeShort(((currentPos.getX() + currentDir.getDirectionX()) << 1) + 1, ByteOrder.LE).writeShort(((currentPos.getY() + currentDir.getDirectionY()) << 1) + 1, ByteOrder.LE);
		} else {
			maskBuf.writeShort((loc.getX() << 1) + 1, ByteOrder.LE).writeShort((loc.getY() << 1) + 1, ByteOrder.LE);
		}
	}
	
	private static void appendTransformationMask(Mob mob, PacketBuilder maskBuf) {
		maskBuf.writeShort(mob.id, ByteModification.ADD, ByteOrder.LE);
	}
	
	private static void appendFirstHitMask(final Mob mob, final PacketBuilder updateBlock) {
		Hit hit = mob.firstHit;
		int id = hit.getHitsplat().getId();
		int max = mob.getMaximumHealth() >= 500 ? 200 : 100;
		int health = max * mob.getCurrentHealth() / mob.getMaximumHealth();
		if(health > max)
			health = max;
		
		if(hit.getHitsplat() == Hitsplat.NORMAL && hit.getDamage() > 0) {
			id++;
		}
		
		updateBlock.writeByte(hit.getDamage());
		updateBlock.writeByte(id, ByteModification.ADD);
		updateBlock.writeByte(hit.getHitIcon().getId());
		updateBlock.writeByte(health);
		updateBlock.writeByte(max, ByteModification.NEG);
	}
	
	private static void appendSecondHitMask(final Mob mob, final PacketBuilder updateBlock) {
		Hit hit = mob.secondHit;
		int id = hit.getHitsplat().getId();
		int max = mob.getMaximumHealth() >= 500 ? 200 : 100;
		int health = mob.getCurrentHealth() * max / mob.getMaximumHealth();
		if(health > max)
			health = max;
		
		if(hit.getHitsplat() == Hitsplat.NORMAL && hit.getDamage() > 0) {
			id++;
		}
		
		updateBlock.writeByte(hit.getDamage());
		updateBlock.writeByte(id, ByteModification.SUB);
		updateBlock.writeByte(hit.getHitIcon().getId());
		updateBlock.writeByte(health);
		updateBlock.writeByte(max, ByteModification.NEG);
	}
	
}