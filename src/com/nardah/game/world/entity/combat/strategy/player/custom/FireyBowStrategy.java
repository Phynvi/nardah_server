package com.nardah.game.world.entity.combat.strategy.player.custom;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.Animation;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

/**
 * Handles the FireyBowStrategy.
 * @author Adam_#6723
 */
public class FireyBowStrategy extends PlayerRangedStrategy {

	private static final FireyBowStrategy INSTANCE = new FireyBowStrategy();
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

	private FireyBowStrategy() {
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
		int level = defender.skills.getMaxLevel(Skill.MAGIC);
		if(level > 360)
			level = 360;
		int a = (3 * level) / 10 - 100;
		int mod = 140 + (3 * level - 10) / 100 - (a * a) / 100;
		if(mod > 140)
			mod = 140;
		return roll * mod / 100;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int roll) {
		int level = defender.skills.getMaxLevel(Skill.MAGIC);
		if(level > 360)
			level = 360;
		int a = (3 * level) / 10 - 140;
		int mod = 250 + (3 * level - 14) / 100 - (a * a) / 100;
		if(mod > 250)
			mod = 250;
		return roll * mod / 100;
	}

	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return 3;
	}

	private static void setProjectiles(CombatProjectile projectile) {
		if(!projectile.getProjectile().isPresent())
			throw new NullPointerException("No Firey bow projectile found.");
		PROJECTILE_1 = projectile.getProjectile().get();
		PROJECTILE_2 = PROJECTILE_1.copy();
		PROJECTILE_2.setDelay(23 + PROJECTILE_1.getDelay());
		PROJECTILE_2.setDuration(23 + PROJECTILE_1.getDuration());
	}

	public static FireyBowStrategy get() {
		return INSTANCE;
	}

}