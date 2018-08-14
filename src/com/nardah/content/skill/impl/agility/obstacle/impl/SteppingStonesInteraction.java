package com.nardah.content.skill.impl.agility.obstacle.impl;

import com.nardah.content.skill.impl.agility.obstacle.ObstacleInteraction;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Position;

public interface SteppingStonesInteraction extends ObstacleInteraction {
	@Override
	default void start(Player player) {
	}

	@Override
	default void onExecution(Player player, Position start, Position end) {
		int dX = end.getX() - player.getPosition().getX();
		int dY = end.getY() - player.getPosition().getY();
		int modX = Integer.signum(dX);
		int modY = Integer.signum(dY);
		int totalSteps = Math.abs(modX) > Math.abs(modY) ? Math.abs(dX) : Math.abs(dY);

		World.schedule(new Task(true, 3) {
			int steps = 0;

			@Override
			public void execute() {
				player.forceMove(1, getAnimation(), 10, 26, new Position(modX, modY), Direction.WEST);
				if(++steps == totalSteps) {
					cancel();
				}
			}
		});
	}

	@Override
	default void onCancellation(Player player) {
	}
}