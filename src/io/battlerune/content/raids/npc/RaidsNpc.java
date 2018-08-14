package io.battlerune.content.raids.npc;

import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.Player;

public interface RaidsNpc {

	/**
	 * Fetches the npc data for the given npc data name
	 * @return
	 */
	Npc[] getNpcs(Player player);
}
