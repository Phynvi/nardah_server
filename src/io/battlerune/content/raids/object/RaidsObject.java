package io.battlerune.content.raids.object;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.object.CustomGameObject;

public interface RaidsObject {

	/**
	 * Returns the object data of the given type object
	 * @param player
	 * @return
	 */
	CustomGameObject[] getObjects(Player player);
}
