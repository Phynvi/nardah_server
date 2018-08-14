package io.battlerune.game.world.entity.combat.attack.listener.other.prayer.strength;

import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;

public class BurstOfStrengthListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyStrengthLevel(Actor attacker, Actor defender, int damage) {
		return damage * 21 / 20;
	}

}
