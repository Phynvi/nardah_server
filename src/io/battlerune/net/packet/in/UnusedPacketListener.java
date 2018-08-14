package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;

@PacketListenerMeta({3, 35, 36, 58, 77, 78, 85, 86, 156, 200, 226, 238, 230})
public class UnusedPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		//        if (player.debug)
		//            player.send(new SendMessage("[UnusedPacketListener] Opcode: " + packet.getOpcode()));
	}
}
