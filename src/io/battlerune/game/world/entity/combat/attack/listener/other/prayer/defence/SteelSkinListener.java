package io.battlerune.game.world.entity.combat.attack.listener.other.prayer.defence;

import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;

public class SteelSkinListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyDefenceLevel(Actor attacker, Actor defender, int damage) {
		return damage * 23 / 20;
	}

}
