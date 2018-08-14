package com.nardah.content.raids.object.impl;

import com.nardah.content.raids.object.RaidsObject;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.CustomGameObject;
import com.nardah.game.world.object.ObjectDirection;
import com.nardah.game.world.object.ObjectType;

public class LingTree implements RaidsObject {

	@Override
	public CustomGameObject[] getObjects(Player player) {
		return new CustomGameObject[]{new CustomGameObject(1, player.getPosition().copy(), ObjectDirection.EAST, ObjectType.INTERACTABLE)};
	}

}
