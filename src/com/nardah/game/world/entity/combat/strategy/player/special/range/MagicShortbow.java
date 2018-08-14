package com.nardah.game.world.entity.combat.strategy.player.special.range;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.Animation;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Handles the magic shortbow weapon special attack.
 * @author Daniel
 * @author Michaael | Chex
 */
public class MagicShortbow extends PlayerRangedStrategy {

	private static final MagicShortbow INSTANCE = new MagicShortbow();
	private static final Animation ANIMATION = new Animation(1074, UpdatePriority.HIGH);
	private static Projectile PROJECTILE_1;
	private static Projectile PROJECTILE_2;

	static {
		try {
			setProjectiles(CombatProjectile.getDefinition("Magic Shortbow"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private MagicShortbow() {
	}

	@Override
	protected void sendStuff(Player attacker, Actor defender) {
		attacker.animate(ANIMATION);
		PROJECTILE_1.send(attacker, defender);
		PROJECTILE_2.send(attacker, defender);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextRangedHit(attacker, defender), nextRangedHit(attacker, defender)};
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll - roll / 4;
	}

	private static void setProjectiles(CombatProjectile projectile) {
		if(!projectile.getProjectile().isPresent())
			throw new NullPointerException("No Magic Shortbow projectile found.");
		PROJECTILE_1 = projectile.getProjectile().get();
		PROJECTILE_2 = PROJECTILE_1.copy();
		PROJECTILE_2.setDelay(23 + PROJECTILE_1.getDelay());
		PROJECTILE_2.setDuration(23 + PROJECTILE_1.getDuration());
	}

	public static MagicShortbow get() {
		return INSTANCE;
	}

}