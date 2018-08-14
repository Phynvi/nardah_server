package com.nardah.game.task.impl;

import com.nardah.game.world.World;
import com.nardah.util.Utility;
import com.nardah.Config;
import com.nardah.game.task.Task;
import com.nardah.net.discord.Discord;

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
