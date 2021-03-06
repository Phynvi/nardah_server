package com.nardah.game.world.entity.combat.strategy.player.special.range;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * @author Michael | Chex
 */
public class Ballista extends PlayerRangedStrategy {
	private static final Animation ANIMATION = new Animation(7222, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(344, 50, UpdatePriority.HIGH);
	private static final Ballista INSTANCE = new Ballista();

	private Ballista() {
	}

	@Override
	public void hitsplat(Player attacker, Actor defender, Hit hit) {
		super.hitsplat(attacker, defender, hit);
		defender.graphic(GRAPHIC);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextRangedHit(attacker, defender)};
	}

	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return 10;
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll * 4 / 3;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int roll) {
		return roll * 5 / 4;
	}

	public static Ballista get() {
		return INSTANCE;
	}

}