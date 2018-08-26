package com.nardah.content.bot.objective.impl;

import com.nardah.content.bot.PlayerBot;
import com.nardah.content.bot.objective.BotObjective;
import com.nardah.content.bot.objective.BotObjectiveListener;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;

public class WildernessDitchObjective implements BotObjectiveListener {

	@Override
	public void init(PlayerBot bot) {
		int x = RandomUtils.inclusive(3428, 3436);
		Position position = Position.create(x, 2943);
		bot.walkTo(position, () -> finish(bot));
	}

	@Override
	public void finish(PlayerBot bot) {
		bot.face(Direction.NORTH);
		bot.forceMove(3, 6132, 33, 60, new Position(0, 4), Direction.NORTH);
		bot.schedule(4, () -> BotObjective.WALK_IN_WILDERNESS.init(bot));
	}

}
