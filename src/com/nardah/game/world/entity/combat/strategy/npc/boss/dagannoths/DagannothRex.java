package com.nardah.game.world.entity.combat.strategy.npc.boss.dagannoths;

import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Michael | Chex
 */
public class DagannothRex extends MultiStrategy {
	private static final CrushMelee CRUSH = new CrushMelee();
	private static final SlashMelee SLASH = new SlashMelee();
	
	private static final CombatStrategy<Mob>[] STRATEGIES = createStrategyArray(CRUSH, SLASH);
	
	public DagannothRex() {
		currentStrategy = randomStrategy(STRATEGIES);
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static final class CrushMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(2853, UpdatePriority.HIGH);
		
		@Override
		public void finishOutgoing(Mob attacker, Actor defender) {
			attacker.getCombat().setFightType(FightType.SCYTHE_JAB);
		}
		
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
	
	private static final class SlashMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(2851, UpdatePriority.HIGH);
		
		@Override
		public void finishOutgoing(Mob attacker, Actor defender) {
			attacker.getCombat().setFightType(FightType.SCYTHE_REAP);
		}
		
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
	
}
