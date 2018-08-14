package com.nardah.game.world.entity.combat.attack.listener;

import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

public class SimplifiedListener<T extends Actor> implements CombatListener<T> {

	public static final CombatListener<Actor> CANT_ATTACK = new SimplifiedListener<Actor>() {
		@Override
		public boolean canAttack(Actor attacker, Actor defender) {
			return false;
		}
	};

	@Override
	public boolean withinDistance(T attacker, Actor defender) {
		return true;
	}

	@Override
	public boolean canOtherAttack(Actor attacker, T defender) {
		return true;
	}

	@Override
	public boolean canAttack(T attacker, Actor defender) {
		return true;
	}

	@Override
	public void start(T attacker, Actor defender, Hit[] hits) {
	}

	@Override
	public void attack(T attacker, Actor defender, Hit hit) {
	}

	@Override
	public void hit(T attacker, Actor defender, Hit hit) {
	}

	@Override
	public void block(Actor attacker, T defender, Hit hit, CombatType combatType) {
	}

	@Override
	public void preDeath(Actor attacker, T defender, Hit hit) {
	}

	@Override
	public void onDeath(Actor attacker, T defender, Hit hit) {
	}

	@Override
	public void preKill(T attacker, Actor defender, Hit hit) {
	}

	@Override
	public void onKill(T attacker, Actor defender, Hit hit) {
	}

	@Override
	public void hitsplat(T attacker, Actor defender, Hit hit) {
	}

	@Override
	public void finishIncoming(Actor attacker, T defender) {
	}

	@Override
	public void finishOutgoing(T attacker, Actor defender) {
	}

}
