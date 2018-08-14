package com.nardah.game.world.entity.actor.move.waypoint;

import com.nardah.game.world.entity.actor.Actor;

public class FollowWaypoint extends Waypoint {
	
	public FollowWaypoint(Actor actor, Actor target) {
		super(actor, target);
	}
	
	@Override
	public void onDestination() {
		actor.movement.reset();
	}
	
}
