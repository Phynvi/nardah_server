package io.battlerune.game.world.entity.mob.movement.waypoint;

import io.battlerune.game.world.entity.mob.Mob;

public class FollowWaypoint extends Waypoint {
	
	public FollowWaypoint(Mob mob, Mob target) {
		super(mob, target);
	}
	
	@Override
	public void onDestination() {
		mob.movement.reset();
	}
	
}
