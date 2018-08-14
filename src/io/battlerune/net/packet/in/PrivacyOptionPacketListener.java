package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.ClientPackets;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;

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
