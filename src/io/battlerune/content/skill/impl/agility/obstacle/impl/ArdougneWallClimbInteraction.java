package io.battlerune.content.skill.impl.agility.obstacle.impl;

import io.battlerune.content.skill.impl.agility.obstacle.ObstacleInteraction;
import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;

public interface ArdougneWallClimbInteraction extends ObstacleInteraction {
	@Override
	default void start(Player player) {
		player.face(new Position(player.getX(), player.getY() + 1));
	}

	@Override
	default void onExecution(Player player, Position start, Position end) {
		World.schedule(new Task(1) {
			int ticks = 0;

			@Override
			protected void execute() {
				switch(ticks++) {
					case 1:
						player.animate(new Animation(737));
						break;
					case 2:
						player.animate(new Animation(737));
						player.move(new Position(start.getX(), start.getY(), 1));
						break;
					case 3:
						player.animate(new Animation(737));
						player.move(new Position(start.getX(), start.getY(), 2));
						break;
					case 4:
						player.animate(new Animation(2588));
						player.move(end);
						cancel();
						break;
				}
			}
		});
	}

	@Override
	default void onCancellation(Player player) {
	}
}