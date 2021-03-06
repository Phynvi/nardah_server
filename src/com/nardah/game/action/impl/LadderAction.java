package com.nardah.game.action.impl;

import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.util.MessageColor;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.GameObject;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;

import java.util.function.Predicate;

/**
 * Created by Daniel on 2017-10-12.
 */
public class LadderAction extends Action<Player> {
	private int count;
	private final GameObject ladder;
	private final Position position;
	private final Predicate<Player> condition;
	private final String message;

	public LadderAction(Player mob, GameObject ladder, Position position) {
		this(mob, ladder, position, null, null);
	}

	public LadderAction(Player mob, GameObject ladder, Position position, Predicate<Player> condition, String message) {
		super(mob, 1, false);
		this.ladder = ladder;
		this.position = position;
		this.condition = condition;
		this.message = message;
	}

	@Override
	protected boolean canSchedule() {
		if(condition != null && !condition.test(getMob())) {
			getMob().send(new SendMessage(message, MessageColor.RED));
			return false;
		}
		return true;
	}

	@Override
	protected void onSchedule() {
	}

	@Override
	public void execute() {
		getMob().locking.lock();
		getMob().face(position);
		getMob().getCombat().reset();

		if(count == 0) {
			String direction = getMob().getHeight() < position.getHeight() ? "up" : "down";
			getMob().send(new SendMessage("You climb " + direction + " the ladder..."));
			getMob().animate(new Animation(828, UpdatePriority.VERY_HIGH));
		} else if(count == 1) {
			getMob().move(position);
			cancel();
		}
		count++;
	}

	@Override
	protected void onCancel(boolean logout) {
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public String getName() {
		return "Ladder action";
	}

	@Override
	public boolean prioritized() {
		return false;
	}
}
