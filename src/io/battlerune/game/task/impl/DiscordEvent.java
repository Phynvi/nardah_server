package io.battlerune.game.task.impl;

import io.battlerune.Config;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.net.discord.Discord;
import io.battlerune.util.Utility;

/**
 * Sends an update message to the discord channel.
 * @author Daniel
 */
public class DiscordEvent extends Task {

	/**
	 * Constructs a new <code>DiscordEvent</code>.
	 */
	public DiscordEvent() {
		super(10000);
	}

	@Override
	protected boolean canSchedule() {
		return !Config.LIVE_SERVER;
	}

	@Override
	public void execute() {
		int size = World.getPlayerCount();
		if(size >= 5) {
			String message = "There are currently " + size + " players online. [Uptime=" + Utility.getUptime() + " ]";
			Discord.communityMessage(message);
		}
	}
}
