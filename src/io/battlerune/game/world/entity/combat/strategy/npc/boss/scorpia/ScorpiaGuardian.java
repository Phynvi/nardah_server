package io.battlerune.game.world.entity.combat.strategy.npc.boss.scorpia;

import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;

public class ScorpiaGuardian extends MultiStrategy {
	
	public ScorpiaGuardian() {
		currentStrategy = NpcMeleeStrategy.get();
	}
	
	@Override
	public boolean canAttack(Npc attacker, Mob defender) {
		return false;
	}
	
}
