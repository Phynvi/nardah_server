package com.nardah.game.world.entity.combat.strategy.npc.boss.scorpia;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;
import com.nardah.util.Stopwatch;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.move.waypoint.Waypoint;

import java.util.concurrent.TimeUnit;

/**
 * @author Daniel
 */
public class Scorpia extends MultiStrategy {
	private boolean hasGuardians;
	
	public Scorpia() {
		currentStrategy = new Melee();
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void hit(Mob attacker, Actor defender, Hit hit) {
		super.hit(attacker, defender, hit);
		if(hasGuardians || attacker.getCurrentHealth() >= 100) {
			return;
		}
		hasGuardians = true;
		for(int i = 0; i < 2; i++) {
			Position spawn = RandomUtils.random(Utility.getInnerBoundaries(attacker));
			Mob guardian = new Guardian(spawn, attacker);
			guardian.register();
			guardian.definition.setRespawnTime(-1);
		}
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private class GuardianWaypoint extends Waypoint {
		final Mob scorpia;
		
		GuardianWaypoint(Guardian guardian, Mob scorpia) {
			super(guardian, scorpia);
			this.scorpia = scorpia;
		}
		
		@Override
		protected void onDestination() {
			CombatProjectile.getDefinition("Scorpia guardian").getProjectile().ifPresent(projectile -> projectile.send(actor, scorpia));
			actor.animate(new Animation(6261));
			scorpia.heal(2);
			((Guardian) actor).lastHeal.reset();
		}
	}
	
	private class Guardian extends Mob {
		
		private final Stopwatch lastHeal = Stopwatch.start();
		
		private Guardian(Position spawn, Mob scorpia) {
			super(6617, spawn);
			setWaypoint(new Waypoint(this, scorpia) {
				@Override
				protected void onDestination() {
					CombatProjectile.getDefinition("Scorpia guardian").getProjectile().ifPresent(projectile -> projectile.send(actor, scorpia));
					lastHeal.reset();
					actor.animate(new Animation(6261));
					scorpia.heal(2);
				}
			});
		}
		
		@Override
		public void sequence() {
			super.sequence();
			long elapsed = lastHeal.elapsedTime(TimeUnit.NANOSECONDS);
			if(elapsed > 15_000) {
				unregister();
			}
		}
	}
	
	private static final class Melee extends NpcMeleeStrategy {
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender, 16)};
		}
	}
}
