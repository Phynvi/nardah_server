package io.battlerune.game.world.entity.combat.strategy.basic;

import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.impl.kraken.KrakenActivity;
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
public abstract class RangedStrategy<T extends Mob> extends CombatStrategy<T> {
	
	@Override
	public boolean withinDistance(T attacker, Mob defender) {
		FightType fightType = attacker.getCombat().getFightType();
		int distance = getAttackDistance(attacker, fightType);
		if(attacker.isPlayer() && Activity.evaluate(attacker.getPlayer(), it -> it instanceof KrakenActivity)) {
			return true;
		}
		return Utility.within(attacker, defender, distance) && SimplePathChecker.checkProjectile(attacker, defender);
	}
	
	@Override
	public int modifyDamage(T attacker, Mob defender, int damage) {
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
