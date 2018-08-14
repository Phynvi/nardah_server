package com.nardah.game.action;

import com.nardah.game.world.entity.actor.Actor;

import java.util.LinkedList;
import java.util.function.Consumer;

public abstract class ConsecutiveAction<T extends Actor> extends Action<T> {
	
	/**
	 * The list of actions.
	 */
	private LinkedList<Consumer<Action<T>>> actions = new LinkedList<>();
	
	/**
	 * Generates a new {@code ConsecutiveAction} object.
	 * @param mob The actor to apply this action to.
	 */
	public ConsecutiveAction(T mob) {
		super(mob, 1, true);
	}
	
	@Override
	public void execute() {
		if(!actions.isEmpty()) {
			Consumer<Action<T>> action = actions.poll();
			action.accept(this);
		}
	}
	
	/**
	 * Adds an action to the {@code actions} list.
	 * @param action The action.
	 */
	protected void add(Consumer<Action<T>> action) {
		actions.add(action);
	}
	
	/**
	 * Adds an action to the {@code actions} list.
	 * @param action The action.
	 */
	protected void addFirst(Consumer<Action<T>> action) {
		actions.addFirst(action);
	}
	
}