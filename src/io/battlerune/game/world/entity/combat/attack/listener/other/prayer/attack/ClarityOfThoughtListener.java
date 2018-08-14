package io.battlerune.game.world.entity.combat.attack.listener.other.prayer.attack;

import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.mob.Mob;

public class ClarityOfThoughtListener extends SimplifiedListener<Mob> {

	@Override
	public int modifyAttackLevel(Mob attacker, Mob defender, int damage) {
		return damage * 21 / 20;
	}

}
