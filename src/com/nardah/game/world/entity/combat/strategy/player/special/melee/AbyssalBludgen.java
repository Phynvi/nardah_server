package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

/**
 * Handles the staff of the dead weapon special attack.
 * @author Daniel
 */
public class AbyssalBludgen extends PlayerMeleeStrategy {

	private static final Animation ANIMATION = new Animation(7010, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1284, UpdatePriority.HIGH);

	private static final AbyssalBludgen INSTANCE = new AbyssalBludgen();

	@Override
	public void hitsplat(Player attacker, Actor defender, Hit hit) {
		super.hitsplat(attacker, defender, hit);
		defender.graphic(GRAPHIC);
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int damage) {
		int level = attacker.skills.getLevel(Skill.PRAYER);
		int max = attacker.skills.getMaxLevel(Skill.PRAYER);
		return damage + damage * (max - level) / 200;
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	public static AbyssalBludgen get() {
		return INSTANCE;
	}

}