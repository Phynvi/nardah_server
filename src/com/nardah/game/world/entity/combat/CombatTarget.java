package com.nardah.game.world.entity.combat;

import com.nardah.game.world.pathfinding.path.SimplePathChecker;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.region.Region;

import java.util.concurrent.TimeUnit;

public class CombatTarget {
	
	/**
	 * The aggression timeout in minutes.
	 */
	private static final int AGGRESSION_TIMEOUT = 5;
	private final Actor actor;
	private Actor target;
	private int distance;
	
	CombatTarget(Actor actor) {
		this.actor = actor;
		this.distance = Integer.MAX_VALUE;
	}
	
	/**
	 * Checks the aggression for this actor if a target is set.
	 */
	void checkAggression(int level, Position spawn) {
		/* No target */
		if(target == null)
			return;
		
		/* The target is unreachable */
		if(distance == Integer.MAX_VALUE)
			return;
		
		/* The actor is too far from spawn */
		if(!Area.inGodwars(actor) && Utility.getDistance(actor, spawn) > Region.VIEW_DISTANCE) {
			Actor trgt = target;
			distance = Integer.MAX_VALUE;
			actor.getCombat().reset();
			if(actor.isNpc() && actor.getNpc().boundaries.length > 0) {
				Position pos = Utility.randomElement(actor.getNpc().boundaries);
//				actor.interact(trgt);
				actor.walkExactlyTo(pos, () -> {
					actor.resetFace();
					resetTarget();
				});
			}
			return;
		}
		
		if(target.skills.getCombatLevel() > level * 2 && !Area.inWilderness(actor))
			return;
		
		int dist = Utility.getDistance(target, actor);
		int aggressionRadius = actor.width() + 5;
		
		if(Area.inGodwars(actor)) {
			aggressionRadius = Region.SIZE;
		}
		
		/* The actor is too far from target */
		if(dist > aggressionRadius) {
			resetTarget();
			actor.resetFace();
			return;
		}
		
		/* The actor is already in combat with the target */
		if(actor.getCombat().isAttacking(target)) {
			System.out.println("I am attempting to attack " + target.getPlayer().getName());
			return;
		}
		
		if(!actor.getCombat().attack(target)) {
			actor.getCombat().reset();
		}
	}
	
	/**
	 * Compares the given actor with the current target. If the give actor is closer
	 * than the current target, the target will be set to the given actor.
	 * @param other the actor to compare to the target
	 */
	void compare(Actor other) {
		int dist = Utility.getDistance(actor, other);
		
		/* The mob is too far from target */
		if(dist > Region.VIEW_DISTANCE || dist >= distance) {
			return;
		}
		
		/* Found a closer target */
		if(!isTarget(other) && SimplePathChecker.checkProjectile(actor, other)) {
			target = other;
			distance = dist;
		}
	}
	
	public static void checkAggression(Player player) {
		if(!player.isVisible())
			return;
		
		if(player.viewport.getNpcsInViewport().isEmpty())
			return;
		
		if(player.getCombat().inCombat() && !Area.inMulti(player))
			return;
		
		for(Mob mob : player.viewport.getNpcsInViewport()) {
			if(mob == null || !mob.isValid())
				continue;

			if (mob.definition == null)
				continue;

			if(!mob.definition.isAttackable() || !mob.definition.isAggressive())
				continue;
			
			if(mob.isDead() || !mob.isVisible() || mob.forceWalking)
				continue;
			
			if(mob.locking.locked())
				continue;
			
			if(player.aggressionTimer.elapsed(AGGRESSION_TIMEOUT, TimeUnit.MINUTES) && !Area.inGodwars(mob)) {
				if(mob.getCombat().isAttacking(player) && !player.getCombat().isAttacking(mob))
					mob.getCombat().reset();
				continue;
			}
			
			mob.getCombat().compare(player);
		}
	}
	
	public void resetTarget() {
		if(actor.isPlayer()) {
			actor.getPlayer().playerAssistant.sendOpponentStatsInterface(false, null);
		}
		target = null;
		distance = Integer.MAX_VALUE;
	}
	
	public boolean isTarget(Actor actor) {
		return actor.equals(target);
	}
	
	public Actor getTarget() {
		return target;
	}
	
	public void setTarget(Actor target) {
		if(actor.isPlayer() && target.isPlayer()) {
			actor.getPlayer().playerAssistant.sendOpponentStatsInterface(true, target.getPlayer());
		}
		this.target = target;
		distance = Utility.getDistance(actor, target);
	}
	
}
