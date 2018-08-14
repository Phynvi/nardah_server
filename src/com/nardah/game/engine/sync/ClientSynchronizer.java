package com.nardah.game.engine.sync;

import com.nardah.game.world.entity.MobList;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;

public interface ClientSynchronizer {

	void synchronize(MobList<Player> players, MobList<Mob> npcs);

}
