package io.battlerune.game.world.entity.combat;

import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.Actor;

import java.util.List;

/**
 * Represents a combat impact hit effect.
 * @author Artem Batutin
 */
public interface CombatImpact {
	
	/**
	 * Condition if the impact is affecting.
	 */
	default boolean canAffect(Actor attacker, Actor defender, Hit hit) {
		return true;
	}
	
	/**
	 * The impact execution.
	 */
	void impact(Actor attacker, Actor defender, Hit hit, List<Hit> hits);
}
