package com.nardah.game.world.entity.combat.strategy.basic;

import com.nardah.content.activity.Activity;
import com.nardah.content.activity.impl.kraken.KrakenActivity;
import com.nardah.game.world.pathfinding.path.SimplePathChecker;
import com.nardah.util.Utility;
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
public abstract class RangedStrategy<T extends Actor> extends CombatStrategy<T> {
	
	@Override
	public boolean withinDistance(T attacker, Actor defender) {
		FightType fightType = attacker.getCombat().getFightType();
		int distance = getAttackDistance(attacker, fightType);
		if(attacker.isPlayer() && Activity.evaluate(attacker.getPlayer(), it -> it instanceof KrakenActivity)) {
			return true;
		}
		return Utility.within(attacker, defender, distance) && SimplePathChecker.checkProjectile(attacker, defender);
	}
	
	@Override
	public int modifyDamage(T attacker, Actor defender, int damage) {
		if(defender.prayer.isActive(Prayer.PROTECT_FROM_RANGE)) {
			damage *= !attacker.isPlayer() || defender.isNpc() ? 0.0 : 0.6;
		}
		return damage;
	}
	
	protected static void addCombatExperience(Player player, Hit... hits) {
		int exp = 0;
		for(Hit hit : hits) {
			exp += hit.getDamage();
		}
		
		exp *= player.experienceRate;
		if(player.getCombat().getFightType() == FightType.FLARE) {
			exp *= 4;
			player.skills.addExperience(Skill.HITPOINTS, exp / 3);
			player.skills.addExperience(Skill.RANGED, exp);
		} else if(player.getCombat().getFightType() == FightType.SCORCH) {
			exp *= 4;
			player.skills.addExperience(Skill.HITPOINTS, exp / 3);
			player.skills.addExperience(Skill.STRENGTH, exp);
		} else if(player.getCombat().getFightType() == FightType.BLAZE) {
			exp *= 2;
			player.skills.addExperience(Skill.HITPOINTS, exp / 3);
			player.skills.addExperience(Skill.MAGIC, exp);
		} else {
			player.skills.addExperience(Skill.HITPOINTS, exp / 3);
			
			switch(player.getCombat().getFightType().getStyle()) {
				case DEFENSIVE:
					exp /= 2;
					player.skills.addExperience(Skill.RANGED, exp);
					player.skills.addExperience(Skill.DEFENCE, exp);
					break;
				default:
					player.skills.addExperience(Skill.RANGED, exp);
					break;
			}
		}
	}
	
}
