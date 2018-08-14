package io.battlerune.content.activity.impl.pestcontrol;

import io.battlerune.content.activity.ActivityListener;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.Actor;

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
