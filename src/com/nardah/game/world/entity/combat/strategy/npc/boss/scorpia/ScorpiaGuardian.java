package com.nardah.game.world.entity.combat.strategy.npc.boss.scorpia;

import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;

public class ScorpiaGuardian extends MultiStrategy {
	
	public ScorpiaGuardian() {
		currentStrategy = NpcMeleeStrategy.get();
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		return false;
	}
	
}
