package io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

public class WingmanSkree extends MultiStrategy {
	
	public WingmanSkree() {
		currentStrategy = new Magic();
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
	
	private static class Magic extends NpcMagicStrategy {
		public Magic() {
			super(CombatProjectile.getDefinition("Wingman Skree"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, 16)};
		}
	}
	
}
