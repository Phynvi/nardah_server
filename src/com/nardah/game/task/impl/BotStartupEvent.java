package com.nardah.game.task.impl;

import com.nardah.content.bot.PlayerBot;
import com.nardah.content.bot.objective.BotObjective;
import com.nardah.Config;
import com.nardah.game.task.TickableTask;

/**
 * This loads all the bots into the game world after starting the server.
 * @author Daniel
 */
public class BotStartupEvent extends TickableTask {

	public BotStartupEvent() {
		super(false, 1);
	}

	@Override
	protected void tick() {
		if(tick >= Config.MAX_BOTS) {
			cancel();
			return;
		}

		PlayerBot bot = new PlayerBot();
		bot.register();
		BotObjective.WALK_TO_BANK.init(bot);
	}
}
