package com.nardah.game.task.impl;

import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.ForceMovement;
import com.nardah.game.world.position.Position;
import com.nardah.game.Animation;
import com.nardah.game.task.Task;

public class ForceMovementTask extends Task {
	private Actor actor;
	private Position start;
	private Position end;
	private Animation animation;
	private ForceMovement forceMovement;

	private final int moveDelay;
	private int tick;

	public ForceMovementTask(Actor actor, int delay, ForceMovement forceMovement, Animation animation) {
		this(actor, delay, 0, forceMovement, animation);
	}

	public ForceMovementTask(Actor actor, int delay, int moveDelay, ForceMovement forceMovement, Animation animation) {
		super(delay == 0, delay);
		this.actor = actor;
		this.start = forceMovement.getStart().copy();
		this.end = forceMovement.getEnd().copy();
		this.animation = animation;
		this.forceMovement = forceMovement;
		this.moveDelay = moveDelay;
	}

	@Override
	protected boolean canSchedule() {
		return actor.forceMovement == null;
	}

	@Override
	protected void onSchedule() {
		actor.getCombat().reset();
		actor.movement.reset();
		actor.animate(animation);
		actor.setForceMovement(forceMovement);
	}

	@Override
	public void execute() {
		if(tick >= moveDelay) {
			final int x = start.getX() + end.getX();
			final int y = start.getY() + end.getY();
			actor.move(new Position(x, y, actor.getHeight()));
			actor.setForceMovement(null);
			cancel();
		}
		tick++;
	}
}
