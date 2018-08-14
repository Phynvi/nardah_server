package com.nardah.content.activity;

import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.CombatListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

/**
 * A combat listener that is applied to all mobs added to (or removed from) the
 * activity.
 * @param <T> the activity type
 */
public abstract class ActivityListener<T extends Activity> implements CombatListener<Actor> {

	/**
	 * The parent activity.
	 */
	protected final T activity;

	/**
	 * Constructs a new {@code ActivityListener} for a activity.
	 * @param activity the parent activity
	 */
	public ActivityListener(T activity) {
		this.activity = activity;
	}

	@Override
	public boolean withinDistance(Actor attacker, Actor defender) {
		return true;
	}

	@Override
	public boolean canAttack(Actor attacker, Actor defender) {
		return true;
	}

	@Override
	public boolean canOtherAttack(Actor attacker, Actor defender) {
		return true;
	}

	@Override
	public void start(Actor attacker, Actor defender, Hit[] hits) {
	}

	@Override
	public void attack(Actor attacker, Actor defender, Hit hit) {
	}

	@Override
	public void hit(Actor attacker, Actor defender, Hit hit) {
	}

	@Override
	public void block(Actor attacker, Actor defender, Hit hit, CombatType combatType) {
	}

	@Override
	public void preDeath(Actor attacker, Actor defender, Hit hit) {
	}

	@Override
	public void onDeath(Actor attacker, Actor defender, Hit hit) {
	}

	@Override
	public void hitsplat(Actor attacker, Actor defender, Hit hit) {
	}

	@Override
	public void finishOutgoing(Actor attacker, Actor defender) {
	}

	@Override
	public void finishIncoming(Actor attacker, Actor defender) {
	}

	@Override
	public void preKill(Actor attacker, Actor defender, Hit hit) {
	}

	@Override
	public void onKill(Actor attacker, Actor defender, Hit hit) {
	}
}
