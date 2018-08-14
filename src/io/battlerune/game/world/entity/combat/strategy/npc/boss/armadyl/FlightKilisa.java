package io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

public class FlightKilisa extends MultiStrategy {
	
	public FlightKilisa() {
		currentStrategy = new Melee();
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
	
	private static class Melee extends NpcMeleeStrategy {
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender, 18)};
		}
	}
	
}
