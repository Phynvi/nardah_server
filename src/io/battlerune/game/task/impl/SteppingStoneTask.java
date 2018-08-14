package io.battlerune.game.task.impl;

import io.battlerune.game.task.Task;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.object.GameObject;

public abstract class SteppingStoneTask extends Task {

	private final Player player;
	private final GameObject object;
	protected int tick;

	public SteppingStoneTask(Player player, GameObject object) {
		super(true, 0);
		this.player = player;
		this.object = object;
	}

	@Override
	protected void onSchedule() {
		if(!player.getPosition().isWithinDistance(object.getPosition(), 1)) {
			cancel();
			return;
		}
	}

	public abstract void onExecute();

	@Override
	public void execute() {
		onExecute();
		tick++;
	}

	@Override
	protected void onCancel(boolean logout) {

	}

}
