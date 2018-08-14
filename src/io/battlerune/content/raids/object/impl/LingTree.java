package io.battlerune.content.raids.object.impl;

import io.battlerune.content.raids.object.RaidsObject;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.object.ObjectDirection;
import io.battlerune.game.world.object.ObjectType;

public class LingTree implements RaidsObject {

	@Override
	public CustomGameObject[] getObjects(Player player) {
		return new CustomGameObject[]{new CustomGameObject(1, player.getPosition().copy(), ObjectDirection.EAST, ObjectType.INTERACTABLE)};
	}

}
