package com.nardah.game.world.entity.combat.strategy.npc.boss.kril;

import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;

public class Tstanon extends MultiStrategy {
	
	public Tstanon() {
		currentStrategy = new Melee();
	}
	
	private class Melee extends NpcMeleeStrategy {
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender, 15)};
		}
	}
	
}
