package com.nardah.game.world.entity.combat.effect;

import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Actor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Some sort of temporary effect applied to a {@link Actor} during combat. Combat
 * effects include but are not limited to; being poisoned, skulled, and
 * teleblocked.
 * @author lare96 <http://github.org/lare96>
 */
public abstract class CombatEffect {
	
	/**
	 * The map of all of the combat effect types mapped to their respective
	 * listeners.
	 */
	public static final Map<CombatEffectType, CombatEffect> EFFECTS = new HashMap<>();
	
	/**
	 * The delay for this individual combat effect.
	 */
	private final int delay;
	
	/**
	 * Creates a new {@link CombatEffect}.
	 */
	public CombatEffect(int delay) {
		this.delay = delay;
	}
	
	/**
	 * Starts this combat effect by scheduling a task utilizing the abstract methods
	 * in this class.
	 */
	public final boolean start(Actor actor) {
		if(apply(actor)) {
			World.schedule(new CombatEffectTask(actor, this));
			return true;
		}
		return false;
	}
	
	/**
	 * Applies this effect to {@code actor}.
	 */
	public abstract boolean apply(Actor actor);
	
	/**
	 * Removes this effect from {@code actor} if needed.
	 */
	public abstract boolean removeOn(Actor actor);
	
	/**
	 * Provides processing for this effect on {@code actor}.
	 */
	public abstract void process(Actor actor);
	
	/**
	 * Executed on login, primarily used to re-apply the effect to {@code actor}.
	 */
	public abstract boolean onLogin(Actor actor);
	
	/**
	 * Gets the delay for this individual combat effect.
	 */
	protected final int getDelay() {
		return delay;
	}
	
	/**
	 * Returns an unmodifiable view of the combat effect listeners.
	 */
	public static Collection<CombatEffect> values() {
		return Collections.unmodifiableCollection(EFFECTS.values());
	}
}
