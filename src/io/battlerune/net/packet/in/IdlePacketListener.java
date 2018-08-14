package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;

@PacketListenerMeta({0})
public class IdlePacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {

	}

}
