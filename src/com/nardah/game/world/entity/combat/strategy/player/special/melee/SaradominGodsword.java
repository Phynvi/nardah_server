package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

import static com.nardah.game.world.entity.skill.Skill.HITPOINTS;
import static com.nardah.game.world.entity.skill.Skill.PRAYER;

/**
 * Handles the saradomin sword weapon special attack.
 * @author Daniel
 */
public class SaradominGodsword extends PlayerMeleeStrategy {

	// SGS(normal): 7640, SGS(OR): 7641
	private static final Animation ANIMATION = new Animation(7640, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1209, UpdatePriority.HIGH);

	private static final SaradominGodsword INSTANCE = new SaradominGodsword();

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);
		attacker.graphic(GRAPHIC);
	}

	@Override
	public void hit(Player attacker, Actor defender, Hit hit) {
		super.hit(attacker, defender, hit);

		int heal = hit.getDamage() / 2;
		int prayerRestore = hit.getDamage() / 4;

		Skill skill = attacker.skills.get(HITPOINTS);
		if(skill.getLevel() < skill.getMaxLevel()) {
			int level = skill.getLevel() + heal;
			if(skill.getLevel() + heal > skill.getMaxLevel())
				level = skill.getMaxLevel();
			attacker.skills.setLevel(HITPOINTS, level);
		}

		skill = attacker.skills.get(PRAYER);
		if(skill.getLevel() < skill.getMaxLevel()) {
			int level = skill.getLevel() + prayerRestore;
			if(skill.getLevel() + prayerRestore > skill.getMaxLevel())
				level = skill.getMaxLevel();
			attacker.skills.setLevel(PRAYER, level);
		}
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextMeleeHit(attacker, defender)};
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll * 2;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int damage) {
		return damage * 11 / 10;
	}

	public static SaradominGodsword get() {
		return INSTANCE;
	}

}