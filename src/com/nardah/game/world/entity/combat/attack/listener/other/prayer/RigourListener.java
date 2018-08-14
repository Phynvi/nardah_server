package com.nardah.game.world.entity.combat.attack.listener.other.prayer;

import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

public class RigourListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyRangedLevel(Actor attacker, Actor defender, int level) {
		return level * 6 / 5;
	}

	@Override
	public int modifyDamage(Actor attacker, Actor defender, int damage) {
		if(attacker.getStrategy().getCombatType() != CombatType.RANGED)
			return damage;
		return damage * 103 / 100;
	}

	@Override
	public int modifyDefenceLevel(Actor attacker, Actor defender, int level) {
		return level * 5 / 4;
	}

}
