package com.nardah.game.world.entity.combat.strategy.basic;

import com.nardah.game.world.pathfinding.path.SimplePathChecker;
import com.nardah.util.Utility;
import com.nardah.Config;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.entity.skill.Skill;

/**
 * @author Michael | Chex
 */
public abstract class MagicStrategy<T extends Actor> extends CombatStrategy<T> {
	
	@Override
	public boolean withinDistance(T attacker, Actor defender) {
		FightType fightType = attacker.getCombat().getFightType();
		int distance = getAttackDistance(attacker, fightType);
		return Utility.within(attacker, defender, distance) && SimplePathChecker.checkProjectile(attacker, defender);
	}
	
	@Override
	public int modifyDamage(T attacker, Actor defender, int damage) {
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
