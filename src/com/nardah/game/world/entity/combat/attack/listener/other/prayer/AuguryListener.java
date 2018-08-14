package com.nardah.game.world.entity.combat.attack.listener.other.prayer;

import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

public class AuguryListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyMagicLevel(Actor attacker, Actor defender, int level) {
		return level * 5 / 4;
	}

	@Override
	public int modifyDefenceLevel(Actor attacker, Actor defender, int damage) {
		return damage * 5 / 4;
	}

}
