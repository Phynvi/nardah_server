package com.nardah.game.world.entity.combat.attack.listener.other.prayer;

import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

public class PietyListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyAttackLevel(Actor attacker, Actor defender, int level) {
		return level * 6 / 5;
	}

	@Override
	public int modifyStrengthLevel(Actor attacker, Actor defender, int level) {
		return level * 123 / 100;
	}

	@Override
	public int modifyDefenceLevel(Actor attacker, Actor defender, int level) {
		return level * 5 / 4;
	}

}
