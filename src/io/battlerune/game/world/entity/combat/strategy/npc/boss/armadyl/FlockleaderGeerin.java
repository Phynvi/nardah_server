package io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

public class FlockleaderGeerin extends MultiStrategy {
	
	public FlockleaderGeerin() {
		currentStrategy = new Ranged();
	}
	
	@Override
	public boolean canOtherAttack(Actor attacker, Npc defender) {
		if(attacker.isPlayer() && attacker.getStrategy().getCombatType().equals(CombatType.MELEE)) {
			attacker.getPlayer().message("You can't attack Armadyl with melee!");
			return false;
		}
		return super.canOtherAttack(attacker, defender);
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class Ranged extends NpcRangedStrategy {
		public Ranged() {
			super(CombatProjectile.getDefinition("Flockleader Geerin"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 25)};
		}
	}
	
}
