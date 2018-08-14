package com.nardah.net.packet.in;

import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

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