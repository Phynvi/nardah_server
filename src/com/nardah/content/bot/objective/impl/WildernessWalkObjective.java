package com.nardah.content.bot.objective.impl;

import com.nardah.content.bot.PlayerBot;
import com.nardah.content.bot.objective.BotObjectiveListener;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;
import javafx.geometry.Pos;

public class WildernessWalkObjective implements BotObjectiveListener {

	@Override
	public void init(PlayerBot bot) {
		bot.loop(1, () -> {
			if(bot.movement.needsPlacement()) {
				return;
			}

			int x = bot.getX() + RandomUtils.inclusive(-5, 5);
			int y = bot.getY() + RandomUtils.inclusive(-5, 5);
			if(x < 3061)
				x = 3061;
			if(y < 3525)
				y = 3525;
			if(x > 3101)
				x = 3101;
			if(y > 3547)
				y = 3547;

			Position pos = new Position(3429, 2940);
			bot.walkExactlyTo(Position.create(pos.getX(), pos.getY()));
			bot.pause(RandomUtils.inclusive(4, 15));
		});
	}

	@Override
	public void finish(PlayerBot bot) {
	}

}
