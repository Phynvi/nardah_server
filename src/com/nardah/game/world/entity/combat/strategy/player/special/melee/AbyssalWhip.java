package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Handles the abyssal whip weapon special attack.
 * @author Daniel
 */
public class AbyssalWhip extends PlayerMeleeStrategy {
	private static final Animation ANIMATION = new Animation(1658, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(341);
	private static final AbyssalWhip INSTANCE = new AbyssalWhip();

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);
		defender.graphic(GRAPHIC);
	}

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);
		// TODO EFFECT
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
		return roll * 5 / 3;
	}

	public static AbyssalWhip get() {
		return INSTANCE;
	}

}