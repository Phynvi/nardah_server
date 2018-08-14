package com.nardah.content.activity.impl.pestcontrol;

import com.nardah.content.activity.ActivityListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

/**
 * Created by Daniel on 2017-09-29.
 */
public class PestControlListener extends ActivityListener<PestControl> {

	PestControlListener(PestControl activity) {
		super(activity);
	}

	@Override
	public void hit(Actor attacker, Actor defender, Hit hit) {
	}

	@Override
	public void onDeath(Actor attacker, Actor defender, Hit hit) {
	}
}
