package io.battlerune.game.action.impl;

import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.action.Action;
import io.battlerune.game.action.policy.WalkablePolicy;
import io.battlerune.game.world.entity.mob.Direction;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;

import java.util.function.Predicate;

/**
 * Created by Daniel on 2017-10-12.
 */
public class LeverAction extends Action<Player> {
	private int count;
	private final GameObject lever;
	private final Position position;
	private final Direction face;
	private final Predicate<Player> condition;
	private final String message;

	public LeverAction(Player mob, GameObject lever, Position position, Direction face) {
		this(mob, lever, position, face, null, null);
	}

	private LeverAction(Player mob, GameObject lever, Position position, Direction face, Predicate<Player> condition, String message) {
		super(mob, 1, false);
		this.lever = lever;
		this.position = position;
		this.face = face;
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
		getMob().face(face);
		getMob().getCombat().reset();

		if(count == 0) {
			getMob().send(new SendMessage("You pull the lever..."));
			getMob().animate(new Animation(2140, UpdatePriority.VERY_HIGH));
		} else if(count == 1) {
			Teleportation.teleportNoChecks(getMob(), position, Teleportation.TeleportationData.MODERN);
			cancel();
		}
		count++;
	}

	@Override
	protected void onCancel(boolean logout) {
		getMob().locking.unlock();
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public String getName() {
		return "Lever action";
	}

	@Override
	public boolean prioritized() {
		return false;
	}
}
