package com.nardah.net.packet.in;

import com.nardah.content.clanchannel.content.ClanTaskKey;
import com.nardah.game.event.impl.CommandEvent;
import com.nardah.game.plugin.PluginManager;
import com.nardah.game.world.entity.actor.player.command.CommandParser;
import com.nardah.game.world.entity.actor.player.relations.ChatMessage;
import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@code GamePacket} responsible for handling user commands send from the
 * client.
 * @author Michael | Chex
 */
@PacketListenerMeta(ClientPackets.PLAYER_COMMAND)
public final class CommandPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		final String input = packet.getRS2String().trim().toLowerCase();

		if(input.isEmpty() || input.length() > ChatMessage.CHARACTER_LIMIT) {
			return;
		}

		final CommandParser parser = CommandParser.split(input, " ");

		if(parser.getCommand().startsWith("/")) {
			if(player.punishment.isMuted()) {
				player.message("You can not send clan messages while muted!");
				return;
			}

			player.forClan(channel -> {
				CommandParser copy = CommandParser.split(input, "/");
				if(copy.hasNext()) {
					final String line = copy.nextLine();
					channel.chat(player.getName(), Utility.capitalizeSentence(line));
				}
			});
			return;
		}

		player.forClan(channel -> channel.activateTask(ClanTaskKey.SEND_CLAN_MESSAGE, player.getName()));

		PluginManager.getDataBus().publish(player, new CommandEvent(parser));
	}

}
