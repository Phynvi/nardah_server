package com.nardah.game.world.entity.combat.attack.listener.other.prayer.ranged;

import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

public class HawkEyeListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyRangedLevel(Actor attacker, Actor defender, int damage) {
		return damage * 11 / 10;
	}

}
