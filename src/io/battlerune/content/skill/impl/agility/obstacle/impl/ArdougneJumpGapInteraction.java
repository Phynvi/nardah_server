package io.battlerune.content.skill.impl.agility.obstacle.impl;

import io.battlerune.content.skill.impl.agility.obstacle.ObstacleInteraction;
import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;

public interface ArdougneJumpGapInteraction extends ObstacleInteraction {
	@Override
	default void start(Player player) {
	}

	@Override
	default void onExecution(Player player, Position start, Position end) {
		World.schedule(new Task(1) {
			int ticks = 0;

			@Override
			public void execute() {
				switch(ticks++) {
					case 1:
						player.face(end);
						player.animate(new Animation(2586));
						break;
					case 2:
						player.move(end);
						player.animate(new Animation(2588));
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
