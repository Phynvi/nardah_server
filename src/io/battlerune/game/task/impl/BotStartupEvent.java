package io.battlerune.game.task.impl;

import io.battlerune.Config;
import io.battlerune.content.bot.PlayerBot;
import io.battlerune.content.bot.objective.BotObjective;
import io.battlerune.game.task.TickableTask;

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
