package com.nardah.game.engine.sync;

import com.nardah.game.engine.sync.task.*;
import io.battlerune.game.engine.sync.task.*;
import com.nardah.game.world.entity.MobList;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;

public final class SequentialClientSynchronizer implements ClientSynchronizer {

	@Override
	public void synchronize(MobList<Player> players, MobList<Mob> npcs) {
		players.forEach(player -> new PlayerPreUpdateTask(player).run());
		npcs.forEach(npc -> new NpcPreUpdateTask(npc).run());

		players.forEach(player -> new PlayerUpdateTask(player).run());
		players.forEach(player -> new NpcUpdateTask(player).run());

		players.forEach(player -> new PlayerPostUpdateTask(player).run());
		npcs.forEach(npc -> new NpcPostUpdateTask(npc).run());
	}

}
