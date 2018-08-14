package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.FormulaFactory;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.HitIcon;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * @author Michael | Chex
 */
public class DragonClaws extends PlayerMeleeStrategy {
	private static final Animation ANIMATION = new Animation(7527, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1171, 50);

	private static final DragonClaws INSTANCE = new DragonClaws();

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);
		attacker.graphic(GRAPHIC);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		CombatHit first = nextMeleeHit(attacker, defender);

		if(first.getDamage() < 1) {
			return secondOption(attacker, defender, first);
		}

		CombatHit second = first.copyAndModify(damage -> damage / 2);
		CombatHit third = second.copyAndModify(damage -> damage / 2);
		CombatHit fourth = second.copyAndModify(damage -> first.getDamage() - second.getDamage() - third.getDamage());
		return new CombatHit[]{first, second, third, fourth};
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	private CombatHit[] secondOption(Player attacker, Actor defender, CombatHit inaccurate) {
		CombatHit second = nextMeleeHit(attacker, defender);

		if(second.getDamage() < 1) {
			return thirdOption(attacker, defender, inaccurate, second);
		}

		CombatHit third = second.copyAndModify(damage -> damage / 2);
		return new CombatHit[]{inaccurate, second, third, third};
	}

	private CombatHit[] thirdOption(Player attacker, Actor defender, CombatHit inaccurate, CombatHit inaccurate2) {
		CombatHit third = nextMeleeHit(attacker, defender);

		if(third.getDamage() < 1) {
			return fourthOption(attacker, defender, inaccurate, inaccurate2);
		}
		int maxHit = FormulaFactory.getModifiedMaxHit(attacker, defender, getCombatType()) * 3 / 4;
		maxHit = attacker.getCombat().modifyDamage(defender, maxHit);
		third.setDamage(maxHit);
		CombatHit fourth = third.copyAndModify(damage -> third.getDamage());
		return new CombatHit[]{inaccurate, inaccurate2, third, fourth};
	}

	private CombatHit[] fourthOption(Player attacker, Actor defender, CombatHit inaccurate, CombatHit inaccurate2) {
		CombatHit fourth = nextMeleeHit(attacker, defender);

		if(fourth.getDamage() < 1) {
			int hitDelay = CombatUtil.getHitDelay(attacker, defender, getCombatType());
			CombatHit hit = new CombatHit(new Hit(10, HitIcon.MELEE), hitDelay, 0);
			return new CombatHit[]{inaccurate, inaccurate2, hit, hit};
		}

		fourth.modifyDamage(damage -> (int) (damage * 1.50));
		return new CombatHit[]{inaccurate, inaccurate2, fourth, fourth};
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll * 7 / 4;
	}

	public static DragonClaws get() {
		return INSTANCE;
	}

}
