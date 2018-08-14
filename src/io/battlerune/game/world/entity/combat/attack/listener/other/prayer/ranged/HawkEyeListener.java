package io.battlerune.game.world.entity.combat.attack.listener.other.prayer.ranged;

import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;

public class HawkEyeListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyRangedLevel(Actor attacker, Actor defender, int damage) {
		return damage * 11 / 10;
	}

}
