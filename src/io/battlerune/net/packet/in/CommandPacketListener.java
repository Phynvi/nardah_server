package io.battlerune.net.packet.in;

import io.battlerune.content.clanchannel.content.ClanTaskKey;
import io.battlerune.game.event.impl.CommandEvent;
import io.battlerune.game.plugin.PluginManager;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.command.CommandParser;
import io.battlerune.game.world.entity.mob.player.relations.ChatMessage;
import io.battlerune.net.packet.ClientPackets;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.util.Utility;

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
