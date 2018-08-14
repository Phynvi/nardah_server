package com.nardah.content.activity.impl.vorkath;

import com.nardah.content.activity.ActivityListener;
import com.nardah.game.world.Interactable;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;

public class VorkathActivityListener extends ActivityListener<VorkathActivity> {

	VorkathActivityListener(VorkathActivity vorkathActivity) {
		super(vorkathActivity);
	}

	@Override
	public boolean withinDistance(Actor attacker, Actor defender) {
		if(!attacker.isPlayer())
			return true;
		FightType fightType = attacker.getCombat().getFightType();
		int distance = attacker.getStrategy().getAttackDistance(attacker, fightType);
		Interactable vorkathh = Interactable.create(new Position(2269, 4062, attacker.getHeight()), 4, 4);
		return Utility.getDistance(attacker, vorkathh) <= distance && attacker.getStrategy().withinDistance(attacker, activity.vorkath);
	}

	@Override
	public boolean canAttack(Actor attacker, Actor defender) {
		return activity.vorkath == null || !activity.vorkath.isDead();
	}

	@Override
	public void hit(Actor attacker, Actor defender, Hit hit) {
		if(!attacker.isPlayer() && !defender.isNpc()) {
			return;
		}
	}

	@Override
	public void onDeath(Actor attacker, Actor defender, Hit hit) {
	}
}