package io.battlerune.content.activity.impl.pestcontrol;

import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.position.Position;

import static io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener.CANT_ATTACK;

class Portal extends Npc {
	Portal(int id, Position position) {
		super(id, position);
		walk = false;
		getCombat().addListener(CANT_ATTACK);
	}

	private void spawn() {
		if(isDead())
			return;
	}
}
