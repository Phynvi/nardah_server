package com.nardah.game.world.entity.combat.strategy.player.special.range;

import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.Animation;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;

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
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);
		if(Utility.random(1, 3) == 1){
			if (hit.getDamage() == 0) {
				hit.setDamage(RandomUtils.inclusive(0, 20));
			}
		}
		attacker.animate(ANIMATION);
		PROJECTILE.send(attacker, defender);
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