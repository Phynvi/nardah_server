package io.battlerune.game.world.entity.combat.strategy.player.special.melee;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;

/**
 * Handles the saradomin sword weapon special attack.
 * @author Daniel
 */
public class SaradominSword extends PlayerMeleeStrategy {

	private static final Animation ANIMATION = new Animation(1132, UpdatePriority.HIGH);

	private static final Graphic OTHER_GRAPHIC = new Graphic(1196);
	private static final Graphic GRAPHIC = new Graphic(1213);

	private static final SaradominSword INSTANCE = new SaradominSword();

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);
		attacker.graphic(GRAPHIC);
	}

	@Override
	public void finishOutgoing(Player attacker, Actor defender) {
		defender.graphic(OTHER_GRAPHIC);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		CombatHit melee = nextMeleeHit(attacker, defender);
		return new CombatHit[]{melee, nextMagicHit(attacker, defender, 16, 1, 0)};
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll * 4 / 3;
	}

	public static SaradominSword get() {
		return INSTANCE;
	}

}
