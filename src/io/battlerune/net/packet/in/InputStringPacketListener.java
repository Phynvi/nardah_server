package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.util.Utility;

/**
 * The {@link GamePacket} responsible for reciving a string sent by the client.
 * @author Michael | Chex
 */
@PacketListenerMeta(60)
public class InputStringPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		String input = Utility.longToString(packet.readLong()).replace("_", " ");

		if(player.enterInputListener.isPresent()) {
			player.enterInputListener.get().accept(input);
			//			player.enterInputListener = Optional.empty();
		}
	}
}