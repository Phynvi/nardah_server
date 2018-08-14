package com.nardah.content.activity.impl.zulrah;

import com.nardah.content.activity.ActivityListener;
import com.nardah.game.world.entity.actor.Actor;

class ZulrahListener extends ActivityListener<ZulrahActivity> {

	ZulrahListener(ZulrahActivity activity) {
		super(activity);
	}

	@Override
	public boolean canOtherAttack(Actor attacker, Actor defender) {
		return true;
	}

	@Override
	public boolean canAttack(Actor attacker, Actor defender) {
		if(attacker.isNpc() && attacker.getNpc().id != 2045) {
			return activity.attackable;
		}
		return true;
	}
}
