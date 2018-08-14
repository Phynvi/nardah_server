package com.nardah.content.skill.impl.agility.obstacle.impl;

import com.nardah.content.skill.impl.agility.obstacle.ObstacleInteraction;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Position;

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