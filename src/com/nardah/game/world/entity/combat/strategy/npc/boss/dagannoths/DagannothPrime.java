package com.nardah.game.world.entity.combat.strategy.npc.boss.dagannoths;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;

/**
 * @author Michael | Chex
 */
public class DagannothPrime extends MultiStrategy {
	
	public DagannothPrime() {
		currentStrategy = new WaterWave();
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class WaterWave extends NpcMagicStrategy {
		private WaterWave() {
			super(CombatProjectile.getDefinition("Water Wave"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, 50)};
		}
	}
	
}
