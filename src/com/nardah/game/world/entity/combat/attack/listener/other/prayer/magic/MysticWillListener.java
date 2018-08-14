package com.nardah.game.world.entity.combat.attack.listener.other.prayer.magic;

import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

public class MysticWillListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyMagicLevel(Actor attacker, Actor defender, int level) {
		return level * 21 / 20;
	}

}
