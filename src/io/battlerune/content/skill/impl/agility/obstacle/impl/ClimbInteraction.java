package io.battlerune.content.skill.impl.agility.obstacle.impl;

import io.battlerune.content.skill.impl.agility.obstacle.ObstacleInteraction;
import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;

public interface ClimbInteraction extends ObstacleInteraction {
	@Override
	default void start(Player player) {
	}

	@Override
	default void onExecution(Player player, Position start, Position end) {
		player.animate(new Animation(getAnimation()));
		World.schedule(new Task(2) {
			@Override
			public void execute() {
				player.move(end);
				player.animate(new Animation(65535));
				this.cancel();
			}
		});
	}

	@Override
	default void onCancellation(Player player) {
	}
}