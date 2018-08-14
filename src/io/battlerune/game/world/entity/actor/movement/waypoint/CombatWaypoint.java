package io.battlerune.game.world.entity.actor.movement.waypoint;

import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.impl.kraken.KrakenActivity;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.movement.Movement;
import io.battlerune.util.Utility;

public class CombatWaypoint extends Waypoint {

	public CombatWaypoint(Actor actor, Actor target) {
		super(actor, target);
	}

	@Override
	public void onDestination() {
		actor.movement.reset();
	}

	@Override
	protected boolean withinDistance() {
		if(target.equals(actor.getCombat().getDefender())) {
			return actor.isPlayer() && Activity.evaluate(actor.getPlayer(), it -> {
				if(it instanceof KrakenActivity) {
					Actor kraken = ((KrakenActivity) it).kraken;
					return Utility.getDistance(actor, kraken) <= getRadius() && actor.getStrategy().withinDistance(actor, kraken);
				}
				return false;
			}) || Utility.getDistance(actor, target) <= getRadius() && actor.getStrategy().withinDistance(actor, (Actor) target);
		}
		return super.withinDistance();
	}

	@Override
	protected int getRadius() {
		if(target.equals(actor.getCombat().getDefender())) {
			FightType fightType = actor.getCombat().getFightType();
			Movement movement = actor.movement;
			int radius = actor.getStrategy().getAttackDistance(actor, fightType);

			if(movement.needsPlacement() && !actor.locking.locked()) {
				radius++;
				if(movement.isRunning())
					radius++;
			}
			return radius;
		}
		return 1;
	}

}
