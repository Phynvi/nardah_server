package io.battlerune.game.world.entity.combat.strategy.npc.boss.dagannoths;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Michael | Chex
 */
public class DagannothRex extends MultiStrategy {
	private static final CrushMelee CRUSH = new CrushMelee();
	private static final SlashMelee SLASH = new SlashMelee();
	
	private static final CombatStrategy<Npc>[] STRATEGIES = createStrategyArray(CRUSH, SLASH);
	
	public DagannothRex() {
		currentStrategy = randomStrategy(STRATEGIES);
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static final class CrushMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(2853, UpdatePriority.HIGH);
		
		@Override
		public void finishOutgoing(Npc attacker, Actor defender) {
			attacker.getCombat().setFightType(FightType.SCYTHE_JAB);
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 1;
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
	
	private static final class SlashMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(2851, UpdatePriority.HIGH);
		
		@Override
		public void finishOutgoing(Npc attacker, Actor defender) {
			attacker.getCombat().setFightType(FightType.SCYTHE_REAP);
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 1;
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
	
}
