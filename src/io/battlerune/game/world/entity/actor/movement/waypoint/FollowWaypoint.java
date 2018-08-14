package io.battlerune.game.world.entity.actor.movement.waypoint;

import io.battlerune.game.world.entity.actor.Actor;

public class FollowWaypoint extends Waypoint {
	
	public FollowWaypoint(Actor actor, Actor target) {
		super(actor, target);
	}
	
	@Override
	public void onDestination() {
		actor.movement.reset();
	}
	
}
