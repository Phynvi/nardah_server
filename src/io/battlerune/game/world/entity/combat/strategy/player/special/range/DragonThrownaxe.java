package io.battlerune.game.world.entity.combat.strategy.player.special.range;

import io.battlerune.game.Animation;
import io.battlerune.game.Projectile;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;

/**
 * Handles the magic shortbow weapon special attack.
 * @author Daniel
 * @author Michaael | Chex
 */
public class DragonThrownaxe extends PlayerRangedStrategy {
	private static final DragonThrownaxe INSTANCE = new DragonThrownaxe();
	private static final Animation ANIMATION = new Animation(4230, UpdatePriority.HIGH);
	private static Projectile PROJECTILE;

	static {
		try {
			setProjectiles(CombatProjectile.getDefinition("Dragon thrownaxe"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private DragonThrownaxe() {
	}

	@Override
	protected void sendStuff(Player attacker, Actor defender) {
		attacker.animate(ANIMATION);
		PROJECTILE.send(attacker, defender);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextRangedHit(attacker, defender)};
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll * 6 / 5;
	}

	private static void setProjectiles(CombatProjectile projectile) {
		if(!projectile.getProjectile().isPresent())
			throw new NullPointerException("No Dragon Thrownaxe projectile found.");
		PROJECTILE = projectile.getProjectile().get();
	}

	public static DragonThrownaxe get() {
		return INSTANCE;
	}

}