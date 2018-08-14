package io.battlerune.game.world.entity.actor.movement.waypoint;

import io.battlerune.game.world.Interactable;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.util.Utility;

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
