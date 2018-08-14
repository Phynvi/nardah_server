package com.nardah.net.packet.in;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

@PacketListenerMeta({0})
public class IdlePacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {

	}

}
