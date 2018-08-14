package com.nardah.content.raids.object;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.CustomGameObject;

public interface RaidsObject {

	/**
	 * Returns the object data of the given type object
	 * @param player
	 * @return
	 */
	CustomGameObject[] getObjects(Player player);
}
