package io.battlerune.net.packet.in;

import io.battlerune.content.event.EventDispatcher;
import io.battlerune.content.event.impl.PickupItemInteractionEvent;
import io.battlerune.game.world.entity.mob.data.PacketType;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.ClientPackets;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.out.SendMessage;

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
