package com.nardah.net.packet.in;

import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.relations.PrivateChatMessage;
import com.nardah.util.ChatCodec;
import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

import java.util.Optional;

/**
 * The {@link GamePacket}'s responsible for player communication.
 * @author Daniel | Obey
 */
@PacketListenerMeta({ClientPackets.ADD_FRIEND, ClientPackets.PRIVATE_MESSAGE, ClientPackets.REMOVE_FRIEND, ClientPackets.REMOVE_IGNORE, ClientPackets.ADD_IGNORE})
public final class PlayerRelationPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		final long username = packet.readLong();
		switch(packet.getOpcode()) {

			case ClientPackets.ADD_FRIEND:
				player.relations.addFriend(username);
				break;

			case ClientPackets.REMOVE_FRIEND:
				player.relations.deleteFriend(username);
				break;

			case ClientPackets.ADD_IGNORE:
				player.relations.addIgnore(username);
				break;

			case ClientPackets.REMOVE_IGNORE:
				player.relations.deleteIgnore(username);
				break;

			case ClientPackets.PRIVATE_MESSAGE:
				final Optional<Player> result = World.search(Utility.formatText(Utility.longToString(username)).replace("_", " "));

				if(!result.isPresent()) {
					break;
				}

				final Player other = result.get();

				final byte[] input = packet.readBytes(packet.getSize() - Long.BYTES);
				final String decoded = ChatCodec.decode(input);
				final byte[] compressed = ChatCodec.encode(decoded);
				player.relations.message(other, new PrivateChatMessage(decoded, compressed));
				break;
		}

	}

}
