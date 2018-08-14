package io.battlerune.game.world.entity.combat.strategy.basic;

import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.HitIcon;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.prayer.Prayer;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.pathfinding.path.SimplePathChecker;
import io.battlerune.util.Utility;

/**
 * @author Michael | Chex
 */
public abstract class MeleeStrategy<T extends Actor> extends CombatStrategy<T> {
	
	@Override
	public boolean withinDistance(T attacker, Actor defender) {
		FightType fightType = attacker.getCombat().getFightType();
		int distance = getAttackDistance(attacker, fightType);
		
		if(attacker.movement.needsPlacement() && defender.movement.needsPlacement() && !attacker.locking.locked()) {
			distance++;
			if(defender.movement.isRunning())
				distance++;
		}
		
		if(defender.id == 1739 || defender.id == 1740 || defender.id == 1741 || defender.id == 1742) {
			return Utility.withinDistance(attacker, defender, distance);
		}
		return Utility.withinDistance(attacker, defender, distance) && SimplePathChecker.checkLine(attacker, defender);
	}
	
	@Override
	public int modifyDamage(T attacker, Actor defender, int damage) {
		if(defender.prayer.isActive(Prayer.PROTECT_FROM_MELEE)) {
			if(defender.attributes.has("VERACS-EFFECT")) {
				return damage;
			}
			
			//            if (attacker.isPlayer()) {
			//                Player player = attacker.getPlayer();
			//                if (player.equipment.retrieve(Equipment.WEAPON_SLOT)
			//                        .filter(item -> item.matchesId(11791) || item.matchesId(12904)).isPresent()
			//                        && System.currentTimeMillis() - player.staffOfDeadSpecial <= 60_000) {
			//                    damage /= 2;
			//                }
			//            }
			
			damage *= !attacker.isPlayer() || defender.isNpc() ? 0.0 : 0.6;
		}
		return damage;
	}
	
	protected static void addCombatExperience(Player player, Hit... hits) {
		int exp = 0;
		for(Hit hit : hits) {
			if(hit.getHitIcon() == HitIcon.MELEE) {
				exp += hit.getDamage();
			} else if(hit.getHitIcon() == HitIcon.MAGIC) {
				MagicStrategy.addCombatExperience(player, 0, hit);
			}
		}
		
		exp *= player.experienceRate;
		player.skills.addExperience(Skill.HITPOINTS, exp / 3);
		switch(player.getCombat().getFightType().getStyle()) {
			case ACCURATE:
				player.skills.addExperience(Skill.ATTACK, exp);
				break;
			case AGGRESSIVE:
				player.skills.addExperience(Skill.STRENGTH, exp);
				break;
			case DEFENSIVE:
				player.skills.addExperience(Skill.DEFENCE, exp);
				break;
			case CONTROLLED:
				exp /= 3;
				player.skills.addExperience(Skill.ATTACK, exp);
				player.skills.addExperience(Skill.STRENGTH, exp);
				player.skills.addExperience(Skill.DEFENCE, exp);
				break;
		}
	}
	
}
