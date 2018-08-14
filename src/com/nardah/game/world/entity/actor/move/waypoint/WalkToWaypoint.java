package com.nardah.game.world.entity.actor.move.waypoint;

import com.nardah.util.Utility;
import com.nardah.game.world.Interactable;
import com.nardah.game.world.entity.actor.Actor;

public class WalkToWaypoint extends Waypoint {
	private Runnable onDestination;

	public WalkToWaypoint(Actor actor, Interactable target, Runnable onDestination) {
		super(actor, target);
		this.onDestination = onDestination;
	}

	@Override
	public void onDestination() {
		actor.movement.reset();
		actor.face(Utility.findBestInside(actor, target));
		onDestination.run();
		cancel();
	}

}
