package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;

@PacketListenerMeta({241})
public class MouseClickPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {

	}

}
