package io.battlerune.game.world.entity.combat.strategy.npc.boss.kril;

import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

public class Zakln extends MultiStrategy {
	
	public Zakln() {
		currentStrategy = new Ranged();
	}
	
	private class Ranged extends NpcRangedStrategy {
		public Ranged() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 21)};
		}
	}
	
}
