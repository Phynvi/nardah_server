package com.nardah.game.world.entity.combat.strategy.npc.boss.scorpia;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.skill.Skill;

public class ScorpiaOffspring extends MultiStrategy {
	
	public ScorpiaOffspring() {
		currentStrategy = new Ranged();
	}
	
	private static final class Ranged extends NpcRangedStrategy {
		public Ranged() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			super.hit(attacker, defender, hit);
			if(hit.getDamage() > 0) {
				defender.skills.get(Skill.PRAYER).removeLevel(1);
				defender.skills.refresh(Skill.PRAYER);
			}
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 5;
		}
	}
	
}
