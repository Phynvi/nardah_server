package com.nardah.content.skill.impl.agility.obstacle.impl;

import com.nardah.content.skill.impl.agility.obstacle.ObstacleInteraction;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.GameObject;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendObjectAnimation;

public interface RopeSwingInteraction extends ObstacleInteraction {
	@Override
	default void start(Player player) {
	}

	@Override
	default void onExecution(Player player, Position start, Position end) {
		int modX = end.getX() - player.getPosition().getX();
		int modY = end.getY() - player.getPosition().getY();

		Position destination = Position.create(modX, modY);
		Direction direction = Direction.getFollowDirection(player.getPosition(), end);
		GameObject object = player.attributes.get("AGILITY_OBJ", GameObject.class);

		player.send(new SendObjectAnimation(497, object));
		player.forceMove(3, 751, 33, 60, destination, direction);
	}

	@Override
	default void onCancellation(Player player) {
	}
}