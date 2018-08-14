package io.battlerune.content.skill.impl.agility.obstacle.impl;

import io.battlerune.content.skill.impl.agility.obstacle.ObstacleInteraction;
import io.battlerune.game.world.entity.actor.UpdateFlag;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;

public interface RunningStartInteraction extends ObstacleInteraction {
	@Override
	default void start(Player player) {
		player.actorAnimation.setWalk(getAnimation());
	}

	@Override
	default void onExecution(Player player, Position start, Position end) {
		player.getCombat().reset();
		player.movement.walk(end);
		player.updateFlags.add(UpdateFlag.APPEARANCE);
	}

	@Override
	default void onCancellation(Player player) {
	}
}