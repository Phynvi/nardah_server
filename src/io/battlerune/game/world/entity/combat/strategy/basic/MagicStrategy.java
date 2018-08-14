package io.battlerune.game.world.entity.combat.strategy.basic;

import io.battlerune.Config;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.prayer.Prayer;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.pathfinding.path.SimplePathChecker;
import io.battlerune.util.Utility;

/**
 * @author Michael | Chex
 */
public abstract class MagicStrategy<T extends Mob> extends CombatStrategy<T> {
	
	@Override
	public boolean withinDistance(T attacker, Mob defender) {
		FightType fightType = attacker.getCombat().getFightType();
		int distance = getAttackDistance(attacker, fightType);
		return Utility.within(attacker, defender, distance) && SimplePathChecker.checkProjectile(attacker, defender);
	}
	
	@Override
	public int modifyDamage(T attacker, Mob defender, int damage) {
		if(defender.prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
			damage *= (attacker.isNpc() && attacker.id != 319) || defender.isNpc() ? 0.0 : 0.6;
		}
		return damage;
	}
	
	protected static void addCombatExperience(Player player, double base, Hit... hits) {
		int exp = 0;
		for(Hit hit : hits) {
			exp += hit.getDamage();
		}
		
		exp *= Config.COMBAT_MODIFICATION;
		exp += base;
		player.skills.addExperience(Skill.MAGIC, exp);
		player.skills.addExperience(Skill.HITPOINTS, exp / 3);
	}
	
}
