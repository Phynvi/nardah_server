package io.battlerune.game.engine.sync;

import io.battlerune.game.world.entity.MobList;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;

public interface ClientSynchronizer {

	void synchronize(MobList<Player> players, MobList<Npc> npcs);

}
