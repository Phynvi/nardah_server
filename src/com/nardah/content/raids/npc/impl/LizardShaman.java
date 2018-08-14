package com.nardah.content.raids.npc.impl;

import com.nardah.content.raids.npc.RaidsNpc;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Position;

public class LizardShaman implements RaidsNpc {

	@Override
	public Mob[] getNpcs(Player player) {
		return new Mob[]{new Mob(6766, new Position(1, 1, player.playerAssistant.instance()), 2)};
	}

}
