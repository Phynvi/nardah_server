package io.battlerune.game.world.entity.combat.strategy.npc.boss.scorpia;

import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.skill.Skill;

public class ScorpiaOffspring extends MultiStrategy {
	
	public ScorpiaOffspring() {
		currentStrategy = new Ranged();
	}
	
	private static final class Ranged extends NpcRangedStrategy {
		public Ranged() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Npc attacker, Mob defender, Hit hit) {
			super.hit(attacker, defender, hit);
			if(hit.getDamage() > 0) {
				defender.skills.get(Skill.PRAYER).removeLevel(1);
				defender.skills.refresh(Skill.PRAYER);
			}
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 5;
		}
	}
	
}
