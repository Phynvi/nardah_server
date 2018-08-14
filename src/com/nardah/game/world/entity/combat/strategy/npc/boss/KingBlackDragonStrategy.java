package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Michael | Chex
 */
public class KingBlackDragonStrategy extends MultiStrategy {
	private static final StabMelee STAB = new StabMelee();
	private static final CrushMelee CRUSH = new CrushMelee();
	private static final Dragonfire DRAGONFIRE = new Dragonfire();
	private static final Poison POISON = new Poison();
	private static final Freeze FREEZE = new Freeze();
	private static final Shock SHOCK = new Shock();
	
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(CRUSH, STAB, DRAGONFIRE, POISON, FREEZE, SHOCK);
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(DRAGONFIRE, POISON, FREEZE, SHOCK);
	
	public KingBlackDragonStrategy() {
		currentStrategy = randomStrategy(NON_MELEE);
	}
	
	@Override
	public boolean withinDistance(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.withinDistance(attacker, defender);
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.canAttack(attacker, defender)) {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(STAB.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(NON_MELEE);
		}
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static final class CrushMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(80, UpdatePriority.HIGH);
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 1;
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
	
	private static final class StabMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(91, UpdatePriority.HIGH);
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 1;
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
	
	private static final class Dragonfire extends DragonfireStrategy {
		
		Dragonfire() {
			super(CombatProjectile.getDefinition("KBD fire"));
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 65, true)};
		}
	}
	
	private static final class Freeze extends DragonfireStrategy {
		
		Freeze() {
			super(CombatProjectile.getDefinition("KBD freeze"));
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 65, true)};
		}
	}
	
	private static final class Shock extends DragonfireStrategy {
		
		Shock() {
			super(CombatProjectile.getDefinition("KBD shock"));
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 65, true)};
		}
	}
	
	private static final class Poison extends DragonfireStrategy {
		
		Poison() {
			super(CombatProjectile.getDefinition("KBD poison"));
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 65, true)};
		}
	}
	
}
