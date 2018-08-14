package io.battlerune.net.packet.in;

import io.battlerune.game.event.impl.log.ChatLogEvent;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.data.PacketType;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.relations.ChatColor;
import io.battlerune.game.world.entity.mob.player.relations.ChatEffect;
import io.battlerune.game.world.entity.mob.player.relations.ChatMessage;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.ClientPackets;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.ChatCodec;

/**
 * The {@code GamePacket} responsible for chat messages.
 * @author Daniel
 */
@PacketListenerMeta(ClientPackets.CHAT)
public class ChatMessagePacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		final int effect = packet.readByte(false, ByteModification.SUB);
		final int color = packet.readByte(false, ByteModification.SUB);
		final int size = packet.getSize() - 2;

		if(effect < 0 || effect >= ChatEffect.values().length || color < 0 || color >= ChatColor.values().length || size <= 0) {
			return;
		}

		if(player.punishment.isMuted()) {
			player.send(new SendMessage("You are currently muted and can not talk!"));
			return;
		}

		if(player.locking.locked(PacketType.CHAT)) {
			return;
		}

		if(player.idle) {
			player.idle = false;
		}

		final String decoded = ChatCodec.decode(packet.readBytesReverse(size, ByteModification.ADD));

		player.chat(ChatMessage.create(decoded, ChatColor.values()[color], ChatEffect.values()[effect]));
		World.getDataBus().publish(new ChatLogEvent(player, decoded));
	}
}