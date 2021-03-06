package com.nardah.game.world.entity.combat.attack.listener.other.prayer.attack;

import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

public class ImprovedReflexesListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyAttackLevel(Actor attacker, Actor defender, int damage) {
		return damage * 11 / 10;
	}

}
