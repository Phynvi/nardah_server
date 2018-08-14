package com.nardah.game.world.entity.combat.strategy.npc.boss.dagannoths;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Michael | Chex
 */
public class Spinolyp extends MultiStrategy {
	private static final Ranged RANGED = new Ranged();
	private static final Magic MAGIC = new Magic();
	
	private static final CombatStrategy<Mob>[] STRATEGIES = createStrategyArray(RANGED, MAGIC);
	
	public Spinolyp() {
		currentStrategy = randomStrategy(STRATEGIES);
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static final class Magic extends NpcMagicStrategy {
		
		public Magic() {
			super(CombatProjectile.getDefinition("Water Strike"));
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 15;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, 10)};
		}
	}
	
	private static final class Ranged extends NpcRangedStrategy {
		
		public Ranged() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 15;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 10)};
		}
	}
	
}
