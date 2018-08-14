package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel | Obey
 */
public class DinhsBulwark extends PlayerMeleeStrategy {
	private static final Graphic GRAPHIC = new Graphic(1292, UpdatePriority.HIGH);
	private static final DinhsBulwark INSTANCE = new DinhsBulwark();

	private DinhsBulwark() {
	}

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		attacker.getCombatSpecial().drain(attacker);
		attacker.animate(getAttackAnimation(attacker, defender));

		List<Hit> extra = new LinkedList<>();
		CombatUtil.areaAction(attacker, 10, 11, other -> hitEvent(attacker, defender, other, extra));

		if(!defender.isPlayer() || !PlayerRight.isIronman(attacker)) {
			Collections.addAll(extra, hits);
			addCombatExperience(attacker, extra.toArray(new Hit[extra.size()]));
		}

		attacker.graphic(GRAPHIC);
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll * 6 / 5;
	}

	private void hitEvent(Player attacker, Actor defender, Actor other, List<Hit> extra) {
		if(!CombatUtil.canBasicAttack(attacker, other)) {
			return;
		}

		if(attacker.equals(other) || defender.equals(other)) {
			return;
		}

		CombatHit hit = nextMeleeHit(attacker, defender);
		attacker.getCombat().submitHits(other, hit);
		if(extra != null)
			extra.add(hit);
	}

	public static DinhsBulwark get() {
		return INSTANCE;
	}

}