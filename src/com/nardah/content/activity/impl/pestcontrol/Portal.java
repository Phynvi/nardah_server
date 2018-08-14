package com.nardah.content.activity.impl.pestcontrol;

import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.position.Position;

import static com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener.CANT_ATTACK;

class Portal extends Mob {
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
