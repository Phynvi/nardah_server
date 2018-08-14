package io.battlerune.game.world.entity.combat.effect;

import io.battlerune.game.task.Task;
import io.battlerune.game.world.entity.actor.Actor;

/**
 * The {@link Task} implementation that provides processing for
 * {@link CombatEffect}s.
 * @author lare96 <http://github.org/lare96>
 */
final class CombatEffectTask extends Task {
	
	/**
	 * The actor that this task is for.
	 */
	private final Actor actor;
	
	/**
	 * The combat effect that is being processed.
	 */
	private final CombatEffect effect;
	
	/**
	 * Creates a new {@link CombatEffectTask}.
	 * @param actor the actor that this task is for.
	 * @param effect the combat effect that is being processed.
	 */
	CombatEffectTask(Actor actor, CombatEffect effect) {
		super(false, effect.getDelay());
		super.attach(actor);
		this.actor = actor;
		this.effect = effect;
	}
	
	@Override
	public void execute() {
		if(effect.removeOn(actor) || !actor.isValid()) {
			cancel();
			return;
		}
		
		effect.process(actor);
	}
	
}
