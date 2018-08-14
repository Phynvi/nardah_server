package com.nardah.net.packet.in;

import com.nardah.game.event.impl.log.ChatLogEvent;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.relations.ChatColor;
import com.nardah.game.world.entity.actor.player.relations.ChatEffect;
import com.nardah.game.world.entity.actor.player.relations.ChatMessage;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.ChatCodec;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

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