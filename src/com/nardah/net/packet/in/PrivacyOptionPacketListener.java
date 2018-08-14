package com.nardah.net.packet.in;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

@PacketListenerMeta({ClientPackets.PRIVACY_OPTIONS})
public final class PrivacyOptionPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		final int publicMode = packet.readByte();
		final int privateMode = packet.readByte();
		final int tradeMode = packet.readByte();
		final int clanMode = packet.readByte();
		player.relations.setPrivacyChatModes(publicMode, privateMode, clanMode, tradeMode);
	}

}
