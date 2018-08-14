package com.nardah.net.packet.in;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@link GamePacket} responsible for dialogues.
 * @author Daniel | Obey
 */
@PacketListenerMeta(40)
public class DialoguePacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		player.dialogueFactory.execute();
	}
}
