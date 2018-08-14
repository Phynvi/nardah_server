package io.battlerune.content.raids.npc.impl;

import io.battlerune.content.raids.npc.RaidsNpc;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.position.Position;

public class LizardShaman implements RaidsNpc {

	@Override
	public Npc[] getNpcs(Player player) {
		return new Npc[]{new Npc(6766, new Position(1, 1, player.playerAssistant.instance()), 2)};
	}

}
