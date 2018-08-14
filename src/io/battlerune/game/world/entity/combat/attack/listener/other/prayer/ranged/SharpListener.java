package io.battlerune.game.world.entity.combat.attack.listener.other.prayer.ranged;

import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;

public class SharpListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyRangedLevel(Actor attacker, Actor defender, int damage) {
		return damage * 21 / 20;
	}

}
