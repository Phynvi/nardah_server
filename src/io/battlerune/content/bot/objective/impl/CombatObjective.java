package io.battlerune.content.bot.objective.impl;

import io.battlerune.content.bot.BotUtility;
import io.battlerune.content.bot.PlayerBot;
import io.battlerune.content.bot.objective.BotObjectiveListener;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.util.Utility;

public class CombatObjective implements BotObjectiveListener {

	@Override
	public void init(PlayerBot bot) {
		Player opponent = (Player) bot.getCombat().getLastAggressor();
		bot.botClass.initCombat(opponent, bot);
		bot.getCombat().attack(opponent);
		bot.speak(Utility.randomElement(BotUtility.FIGHT_START_MESSAGES));
		bot.opponent = opponent;
	}

	@Override
	public void finish(PlayerBot bot) {
	}

}
