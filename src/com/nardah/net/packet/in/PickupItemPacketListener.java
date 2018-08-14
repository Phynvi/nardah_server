package com.nardah.net.packet.in;

import com.nardah.content.event.EventDispatcher;
import com.nardah.content.event.impl.PickupItemInteractionEvent;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@link GamePacket} responsible for picking up an item on the ground.
 * @author Daniel | Obey
 */
@PacketListenerMeta(ClientPackets.PICKUP_GROUND_ITEM)
public class PickupItemPacketListener implements PacketListener {

	@Override
	public void handlePacket(final Player player, GamePacket packet) {
		if(player.locking.locked(PacketType.PICKUP_ITEM)) {
			return;
		}

		final int y = packet.readShort(ByteOrder.LE);
		final int id = packet.readShort(false);
		final int x = packet.readShort(ByteOrder.LE);

		final Item item = new Item(id);
		final Position position = Position.create(x, y, player.getHeight());

		if(EventDispatcher.execute(player, new PickupItemInteractionEvent(item, position))) {
			if(PlayerRight.isDeveloper(player) && player.debug) {
				player.send(new SendMessage(String.format("[%s]: item=%d position=%s", PickupItemInteractionEvent.class.getSimpleName(), item.getId(), position.toString())));
			}
			return;
		}

		player.pickup(item, position);
	}
}
