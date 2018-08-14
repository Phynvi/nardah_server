package io.battlerune.content.skill.impl.agility.obstacle.impl;

import io.battlerune.content.skill.impl.agility.obstacle.ObstacleInteraction;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;

public interface WalkInteraction extends ObstacleInteraction {
	@Override
	default void start(Player player) {
		player.actorAnimation.setWalk(getAnimation());
	}

	@Override
	default void onExecution(Player player, Position start, Position end) {
		player.movement.walk(end);
	}

	@Override
	default void onCancellation(Player player) {
	}
}