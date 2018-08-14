package com.nardah.game.task.impl;

import com.nardah.game.world.object.GameObject;
import com.nardah.game.task.Task;
import com.nardah.game.world.entity.actor.player.Player;

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
