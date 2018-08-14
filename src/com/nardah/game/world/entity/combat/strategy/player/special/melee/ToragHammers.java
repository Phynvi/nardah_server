package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * @author Daniel
 */
public class ToragHammers extends PlayerMeleeStrategy {
	private static final Animation ANIMATION = new Animation(2068, UpdatePriority.HIGH);

	private static final ToragHammers INSTANCE = new ToragHammers();

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{new CombatHit(nextMeleeHit(attacker, defender), 0, 1), new CombatHit(nextMeleeHit(attacker, defender), 0, 0)};
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	public static ToragHammers get() {
		return INSTANCE;
	}

}