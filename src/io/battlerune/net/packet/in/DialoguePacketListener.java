package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;

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
