package com.nardah.game.world.entity.combat.attack.listener.other.prayer.defence;

import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

public class RockSkinListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyDefenceLevel(Actor attacker, Actor defender, int damage) {
		return damage * 11 / 10;
	}

}
