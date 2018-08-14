package com.nardah.content.bot.objective.impl;

import com.nardah.content.bot.BotUtility;
import com.nardah.content.bot.PlayerBot;
import com.nardah.content.bot.objective.BotObjectiveListener;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.util.Utility;

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
