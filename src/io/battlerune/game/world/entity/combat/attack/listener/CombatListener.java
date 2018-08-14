package io.battlerune.game.world.entity.combat.attack.listener;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.FormulaModifier;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.Actor;

/**
 * A combat attack is used to describe what the attacking and defending mobs
 * should do in each stage of combat.
 * @author Michael | Chex
 */
public interface CombatListener<T extends Actor> extends FormulaModifier<T> {
	
	boolean withinDistance(T attacker, Actor defender);
	
	/**
	 * Checks if the attacker can attack the defender.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 */
	boolean canAttack(T attacker, Actor defender);
	
	/**
	 * Checks if the attacker can attack the defender.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 */
	boolean canOtherAttack(Actor attacker, T defender);
	
	/**
	 * Called when the strategy initializes.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 */
	default void init(T attacker, Actor defender) {
	}
	
	/**
	 * Called when the strategy starts.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 */
	void start(T attacker, Actor defender, Hit[] hits);
	
	/**
	 * Called when the attacking hit executes on the defender.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit to apply
	 */
	void attack(T attacker, Actor defender, Hit hit);
	
	/**
	 * Called when the attacking actor performs an attack on the defender.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit to apply
	 */
	void hit(T attacker, Actor defender, Hit hit);
	
	/**
	 * Called when the defending actor blocks a hit from the attacker.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit being applied
	 * @param combatType the combat type for this hit
	 */
	void block(Actor attacker, T defender, Hit hit, CombatType combatType);
	
	/**
	 * Called right before the defending actor dies.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit that killed the defender
	 */
	void preDeath(Actor attacker, T defender, Hit hit);
	
	/**
	 * Called when the defending actor dies.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit that killed the defender
	 */
	void onDeath(Actor attacker, T defender, Hit hit);
	
	/**
	 * Called before attacker killed defender.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit that killed the defender
	 */
	void preKill(T attacker, Actor defender, Hit hit);
	
	/**
	 * Called when attacker killed defender.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit that killed the defender
	 */
	void onKill(T attacker, Actor defender, Hit hit);
	
	/**
	 * Called when attacker does the hitsplat
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 * @param hit the hit that killed the defender
	 */
	void hitsplat(T attacker, Actor defender, Hit hit);
	
	/**
	 * Called when the defending actor finishes their strategy's attack.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 */
	void finishOutgoing(T attacker, Actor defender);
	
	/**
	 * Called when the attacking actor finishes their strategy's attack.
	 * @param attacker the attacking actor
	 * @param defender the defending actor
	 */
	void finishIncoming(Actor attacker, T defender);
	
}
