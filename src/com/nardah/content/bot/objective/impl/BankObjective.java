package com.nardah.content.bot.objective.impl;

import com.nardah.content.bot.PlayerBot;
import com.nardah.content.bot.objective.BotObjective;
import com.nardah.content.bot.objective.BotObjectiveListener;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;

public class BankObjective implements BotObjectiveListener {

	/**
	 * The positions of all the bank locations for the bot to access.
	 */
	private static final Position[] BANK_LOCATIONS = {new Position(3435, 2901)/*, new Position(3098, 3493), new Position(3095, 3491), new Position(3095, 3489)*/};

	@Override
	public void init(PlayerBot bot) {
		Position position = /*RandomUtils.random(BANK_LOCATIONS)*/BANK_LOCATIONS[0];
		bot.walkTo(position, () -> finish(bot));
	}

	@Override
	public void finish(PlayerBot bot) {
		bot.schedule(RandomUtils.random(6, 12), () -> BotObjective.RESTOCK.init(bot));
	}

}
