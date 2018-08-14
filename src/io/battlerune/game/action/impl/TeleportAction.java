package io.battlerune.game.action.impl;

import io.battlerune.content.skill.impl.magic.teleport.Teleportation.TeleportationData;
import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.action.Action;
import io.battlerune.game.action.ConsecutiveAction;
import io.battlerune.game.action.policy.WalkablePolicy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.net.packet.out.SendString;

/**
 * Teleports an entity to another part of the world.
 * @author Daniel
 * @author Michael | Chex
 */
public final class TeleportAction extends ConsecutiveAction<Actor> {

	/**
	 * The teleport position.
	 */
	private final Position position;

	/**
	 * The teleportType
	 */
	private final TeleportationData type;

	/**
	 * The destination randomevent
	 */
	private final Runnable onDestination;

	/**
	 * Constructs a new {@code TeleportAction} object.
	 */
	public TeleportAction(Actor entity, Position position, TeleportationData type, Runnable onDestination) {
		super(entity);
		this.position = position;
		this.type = type;
		this.onDestination = onDestination;
	}

	@Override
	protected void onSchedule() {
		if(getMob().isPlayer() && getMob().getPlayer().isTeleblocked()) {
			cancel();
			getMob().getPlayer().message("You are currently under the affects of a teleblock spell and can not teleport!");
			return;
		}

		if(!valid(this)) {
			cancel();
		} else {
			init();
		}
	}

	private void init() {
		add(this::start);
		add(this::move);
		if(type != TeleportationData.TABLET)
			add(this::end);
		add(this::reset);
	}

	private boolean valid(Action<Actor> action) {
		if(type == TeleportationData.HOME && action.getMob().getCombat().inCombat()) {
			if(action.getMob().isPlayer()) {
				action.getMob().getPlayer().send(new SendMessage("You can't teleport home while in combat!"));
			}
			cancel();
			return false;
		}
		return true;
	}

	private void start(Action<Actor> action) {
		if(type != TeleportationData.HOME)
			action.getMob().inTeleport = true;

		action.setDelay(type.getDelay());
		action.getMob().movement.reset();
		type.getStartAnimation().ifPresent(action.getMob()::animate);
		type.getStartGraphic().ifPresent(action.getMob()::graphic);

		if(type.lockMovement()) {
			action.getMob().locking.lock();
		}

		if(type == TeleportationData.TABLET) {
			addFirst(this::startTablet);
		}
	}

	private void startTablet(Action<Actor> action) {
		action.setDelay(type.getDelay());
		type.getEndAnimation().ifPresent(action.getMob()::animate);
	}

	private void move(Action<Actor> action) {
		if(valid(action)) {
			action.setDelay(1);
			action.getMob().move(position);
			action.getMob().inTeleport = true;
			action.getMob().teleporting = true;

			if(action.getMob().isPlayer()) {
				action.getMob().getPlayer().send(new SendString("[CLOSE_MENU]", 0));
			}
		} else {
			reset(action);
		}
	}

	private void end(Action<Actor> action) {
		type.getEndGraphic().ifPresent(action.getMob()::graphic);
		type.getEndAnimation().ifPresent(action.getMob()::animate);

		if(type.getEndAnimation().isPresent() || type.getEndGraphic().isPresent()) {
			action.setDelay(type.getDelay());
		}
	}

	private void reset(Action<Actor> action) {
		action.getMob().inTeleport = false;
		onDestination.run();
		cancel();
	}

	@Override
	protected void onCancel(boolean logout) {
		getMob().animate(Animation.RESET, true);
		getMob().graphic(Graphic.RESET, true);
		getMob().inTeleport = false;
		getMob().teleporting = false;
	}

	@Override
	public String getName() {
		return "teleport_action";
	}

	@Override
	public boolean prioritized() {
		return false;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

}
