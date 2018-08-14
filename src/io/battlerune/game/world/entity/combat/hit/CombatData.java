package io.battlerune.game.world.entity.combat.hit;

import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.actor.Actor;

/**
 * Holds variables to generate combat data of an actor.
 * @author Michael | Chex
 */
public final class CombatData<T extends Actor> {
	
	/**
	 * The attacker.
	 */
	private final T attacker;
	
	/**
	 * The defender.
	 */
	private final Actor defender;
	
	/**
	 * The combat hit.
	 */
	private final CombatHit hit;
	
	/**
	 * The combat strategy.
	 */
	private final CombatStrategy<? super T> strategy;
	
	/**
	 * Whether or not this hit is the last one.
	 */
	private final boolean lastHit;
	
	/**
	 * Constructs a new {@code CombatHitData} object.
	 */
	public CombatData(T attacker, Actor defender, CombatHit hit, CombatStrategy<? super T> strategy, boolean lastHit) {
		this.attacker = attacker;
		this.defender = defender;
		this.hit = hit;
		this.strategy = strategy;
		this.lastHit = lastHit;
	}
	
	/**
	 * @return the attacker
	 */
	public T getAttacker() {
		return attacker;
	}
	
	/**
	 * @return the defender
	 */
	public Actor getDefender() {
		return defender;
	}
	
	/**
	 * @return the hit
	 */
	public CombatHit getHit() {
		return hit;
	}
	
	/**
	 * @return the hit delay.
	 */
	public int getHitDelay() {
		return hit.getHitDelay();
	}
	
	/**
	 * @return the hitsplat delay.
	 */
	public int getHitsplatDelay() {
		return hit.getHitsplatDelay();
	}
	
	/**
	 * @return the combat strategy
	 */
	public CombatStrategy<? super T> getStrategy() {
		return strategy;
	}
	
	/**
	 * @return {@code true} if this hit data is the last hit
	 */
	public boolean isLastHit() {
		return lastHit;
	}
	
}
