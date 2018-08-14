package io.battlerune.content.activity.impl.cerberus;

import io.battlerune.content.activity.ActivityListener;
import io.battlerune.game.world.Interactable;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.Utility;

public class CerberusActivityListener extends ActivityListener<CerberusActivity> {

	CerberusActivityListener(CerberusActivity minigame) {
		super(minigame);
	}

	@Override
	public boolean withinDistance(Actor attacker, Actor defender) {
		if(!attacker.isPlayer())
			return true;
		FightType fightType = attacker.getCombat().getFightType();
		int distance = attacker.getStrategy().getAttackDistance(attacker, fightType);
		Interactable cerberus = Interactable.create(new Position(1238, 1250, attacker.getHeight()), 4, 4);
		return Utility.getDistance(attacker, cerberus) <= distance && attacker.getStrategy().withinDistance(attacker, activity.cerberus);
	}

	@Override
	public boolean canAttack(Actor attacker, Actor defender) {
		return activity.cerberus == null || !activity.cerberus.isDead();
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