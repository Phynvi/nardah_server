package io.battlerune.game.world.entity.combat.attack.listener.other.prayer;

import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;

public class ChivalryListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyAttackLevel(Actor attacker, Actor defender, int level) {
		return level * 23 / 20;
	}

	@Override
	public int modifyStrengthLevel(Actor attacker, Actor defender, int level) {
		return level * 59 / 50;
	}

	@Override
	public int modifyDefenceLevel(Actor attacker, Actor defender, int level) {
		return level * 6 / 5;
	}

}
