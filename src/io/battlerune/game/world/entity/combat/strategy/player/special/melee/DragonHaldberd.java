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
 * Handles the dragon halbard weapon special attack.
 * @author Daniel
 */
public class DragonHaldberd extends PlayerMeleeStrategy {
	private static final DragonHaldberd INSTANCE = new DragonHaldberd();
	private static final Animation ANIMATION = new Animation(1203, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1172, true, UpdatePriority.HIGH);

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);
	}

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);
		attacker.graphic(GRAPHIC);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		if(defender.width() > 1 && defender.length() > 1) {
			CombatHit primary = nextMeleeHit(attacker, defender);
			CombatHit secondary = nextMeleeHit(attacker, defender);
			return new CombatHit[]{primary, secondary};
		}

		return new CombatHit[]{nextMeleeHit(attacker, defender)};
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll - roll / 4;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int damage) {
		return damage * 11 / 10;
	}

	public static DragonHaldberd get() {
		return INSTANCE;
	}

}