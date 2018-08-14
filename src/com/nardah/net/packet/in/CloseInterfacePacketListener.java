package com.nardah.net.packet.in;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

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