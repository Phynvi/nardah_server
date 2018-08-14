package com.nardah.content.raids.npc;

import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;

public interface RaidsNpc {

	/**
	 * Fetches the mob data for the given mob data name
	 * @return
	 */
	Mob[] getNpcs(Player player);
}
