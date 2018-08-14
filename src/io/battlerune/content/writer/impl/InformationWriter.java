package io.battlerune.content.writer.impl;

import io.battlerune.content.writer.InterfaceWriter;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.net.packet.out.SendScrollbar;
import io.battlerune.util.Utility;

/**
 * Class handles writing on the quest tab itemcontainer.
 * @author Daniel
 */
public class InformationWriter extends InterfaceWriter {

	public InformationWriter(Player player) {
		super(player);
	}

	private String[] text = {"<icon=13> <col=FF7000>Game Information:", "     - Date: <col=FFB83F>" + Utility.getDate(), "     - Uptime: <col=FFB83F>" + Utility.getUptime(), "     - PK Bots online: <col=FFB83F>" + World.getBotCount(), "     - Staff online: <col=FFB83F>" + World.getStaffCount(), "     - Players online: <col=FFB83F>" + World.getPlayerCount(), "     - Wilderness online: <col=FFB83F>" + World.getWildernessCount(), "", "<icon=14> <col=FF7000>Player Information:", "     - Username: <col=FFB83F>" + Utility.formatName(player.getName()), "     - Rank: <col=FFB83F>" + PlayerRight.getCrown(player) + " " + player.right.getName(), "     - Created: <col=FFB83F>" + player.created, "     - Total play time: <col=FFB83F>" + Utility.getTime(player.playTime), "     - Session time: <col=FFB83F>" + Utility.getTime(player.sessionPlayTime), "     - Networth: <col=FFB83F>" + Utility.formatPrice(player.playerAssistant.networth()) + " " + (Utility.formatPrice(player.playerAssistant.networth()).endsWith("!") ? "" : "GP"), "     - Money Spent: <col=FFB83F>$" + Utility.formatDigits(player.donation.getSpent()), "     - Donator Credits: <col=FFB83F>" + Utility.formatDigits(player.donation.getCredits()), "     - Royalty Points: <col=FFB83F>" + player.royalty, "     - Royalty Level: <col=FFB83F>" + player.royaltyLevel, "     - Slayer Task: <col=FFB83F>" + (player.slayer.getTask() == null ? "None" : player.slayer.getTask().getName() + "(" + player.slayer.getAmount() + ")"), "     - PK Points: <col=FFB83F>" + Utility.formatDigits(player.pkPoints), "     - Boss Points: <col=FFB83F>" + Utility.formatDigits(player.bossPoints), "     - Trivia Points: <col=FFB83F>" + Utility.formatDigits(player.triviaPoints), "     - Vote Points: <col=FFB83F>" + Utility.formatDigits(player.votePoints), "     - Total Vote: <col=FFB83F>" + Utility.formatDigits(player.totalVotes), "     - Kills: <col=FFB83F>" + player.kill, "     - Deaths: <col=FFB83F> " + player.death, "     - KDR: <col=FFB83F>" + player.playerAssistant.kdr(), "     - Killstreak: <col=FFB83F>" + player.killstreak.streak,};

	private int[][] font = {{29451, 3}, {29459, 3}};

	@Override
	protected int startingIndex() {
		return 29451;
	}

	@Override
	public void scroll() {
		player.send(new SendScrollbar(29450, 530));
	}

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int[][] color() {
		return null;
	}

	@Override
	protected int[][] font() {
		return font;
	}

}
