package io.battlerune.game.world.entity.combat.strategy.npc.boss.dagannoths;

import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;

import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

/**
 * @author Michael | Chex
 */
public class DagannothSupreme extends MultiStrategy {
	
	public DagannothSupreme() {
		currentStrategy = new Ranged();
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Mob defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class Ranged extends NpcRangedStrategy {
		private Ranged() {
			super(getDefinition("Dagannoth SUPREME"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 30)};
		}
	}
	
}
