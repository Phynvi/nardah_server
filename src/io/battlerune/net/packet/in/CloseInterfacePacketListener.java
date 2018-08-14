package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.ClientPackets;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;

/**
 * The {@link GamePacket} responsible for closing interfaces.
 * @author Daniel
 */
@PacketListenerMeta(130)
public class CloseInterfacePacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {

		switch(packet.getOpcode()) {

			case ClientPackets.CLOSE_WINDOW:
				player.interfaceManager.close(false);
				break;
		}
	}
}