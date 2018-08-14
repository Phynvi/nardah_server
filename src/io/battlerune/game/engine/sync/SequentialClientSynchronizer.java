package io.battlerune.game.engine.sync;

import io.battlerune.game.engine.sync.task.*;
import io.battlerune.game.world.entity.MobList;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;

public final class SequentialClientSynchronizer implements ClientSynchronizer {

	@Override
	public void synchronize(MobList<Player> players, MobList<Npc> npcs) {
		players.forEach(player -> new PlayerPreUpdateTask(player).run());
		npcs.forEach(npc -> new NpcPreUpdateTask(npc).run());

		players.forEach(player -> new PlayerUpdateTask(player).run());
		players.forEach(player -> new NpcUpdateTask(player).run());

		players.forEach(player -> new PlayerPostUpdateTask(player).run());
		npcs.forEach(npc -> new NpcPostUpdateTask(npc).run());
	}

}
