package com.nardah.net.packet.in;

import com.nardah.content.itemaction.ItemActionRepository;
import com.nardah.game.event.impl.DropItemEvent;
import com.nardah.game.plugin.PluginManager;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.game.world.items.containers.pricechecker.PriceType;
import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.game.world.position.Area;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@code GamePacket} responsible for dropping items.
 * @author Daniel | Obey
 */
@PacketListenerMeta(ClientPackets.DROP_ITEM)
public class DropItemPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		if(player.isDead() || player.locking.locked(PacketType.DROP_ITEM)) {
			return;
		}

		final int itemId = packet.readShort(false, ByteModification.ADD);
		packet.readByte(false);
		packet.readByte(false);
		final int slot = packet.readShort(false, ByteModification.ADD);
		final Item item = player.inventory.get(slot);

		if(ItemDefinition.get(itemId) == null)
			return;

		player.getCombat().reset();

		if(!player.interfaceManager.isClear())
			player.interfaceManager.close(false);

		if(player.idle)
			player.idle = false;

		if(item == null)
			return;

		if(item.getId() != itemId)
			return;

		if(PluginManager.getDataBus().publish(player, new DropItemEvent(item, slot, player.getPosition().copy())))
			return;

		if(ItemActionRepository.drop(player, item)) {
			if(PlayerRight.isDeveloper(player) && player.debug) {
				player.send(new SendMessage(String.format("[%s]: item=%d amount=%d slot=%d", ItemActionRepository.class.getSimpleName(), item.getId(), item.getAmount(), slot)));
			}
			return;
		}

		if(player.playTime < 3000 && !player.right.isPriviledged(player)) {
			player.message("You can't drop items until you have 30 mins of playtime. " + Utility.getTime((3000 - player.playTime) * 3 / 5) + " mins left.");
			return;
		}

		boolean inWilderness = Area.inWilderness(player);
		if(inWilderness && item.getValue(PriceType.VALUE) >= 500_000) {
			player.dialogueFactory.sendStatement("This is a valuable item, are you sure you want to", "drop it? In a PvP area, this item will be seen", "by everyone when dropped.");
			player.dialogueFactory.sendOption("Yes, drop it.", () -> {
				player.inventory.remove(item, slot, true);
				GroundItem.createGlobal(player, item);
			}, "Nevermind", () -> player.dialogueFactory.clear());
			player.dialogueFactory.execute();
			return;
		} else if(inWilderness) {
			player.inventory.remove(item, slot, true);
			GroundItem.createGlobal(player, item);
			return;
		}

		player.inventory.remove(item, slot, true);
		GroundItem.create(player, item);
	}
}
