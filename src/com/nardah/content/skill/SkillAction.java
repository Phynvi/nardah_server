package com.nardah.content.skill;

import com.nardah.game.Animation;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.position.Position;

import java.util.Optional;

/**
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 19-12-2016.
 */
public abstract class SkillAction extends Action<Actor> {

	/**
	 * The position we should face.
	 */
	private final Optional<Position> position;

	/**
	 * Creates a new {@link Action} randomevent.
	 * @param actor {@link #mob}.
	 * @param position {@link #position}.
	 * @param delay the delay to repeat this action on.
	 * @param instant {@link #instant}.
	 */
	public SkillAction(Actor actor, Optional<Position> position, int delay, boolean instant) {
		super(actor, delay, instant);
		this.position = position;
	}

	/**
	 * Creates a new {@link Action} randomevent.
	 * @param actor {@link #mob}.
	 * @param position {@link #position}.
	 * @param instant {@link #instant}.
	 */
	public SkillAction(Actor actor, Optional<Position> position, boolean instant) {
		this(actor, position, 1, instant);
	}

	/**
	 * Attempts to start the skill.
	 */
	public final void start() {
		// determine if this task can be initialized.
		if(!canInit()) {
			return;
		}

		// reset any other action.
		getMob().action.clearNonWalkableActions();

		// submit this action.
		getMob().action.execute(this, false);
		position.ifPresent(getMob()::face);
	}

	/**
	 * Determines if this action can be initialized.
	 * @return {@code true} if it can, {@code false} otherwise.
	 */
	public abstract boolean canInit();

	/**
	 * Any functionality that should be handled when this action is submitted.
	 */
	public abstract void init();

	/**
	 * The method which is called on intervals of the specified {@code #delay};
	 */
	public abstract void onExecute();

	/**
	 * The skill animation to execute.
	 * @return the skill animation to execute.
	 */
	public abstract Optional<SkillAnimation> animation();

	/**
	 * The experience given from this action.
	 * @return the numerical value representing the amount of experience given.
	 */
	public abstract double experience();

	/**
	 * The skill we should hand to experience to.
	 */
	public abstract int skill();

	/**
	 * Determines if future skill actions from the same type should be ignored.
	 * @return
	 */
	public boolean ignore() {
		return false;
	}

	/**
	 * The animation counter of this task.
	 */
	private int animationCounter;

	@Override
	protected final void onSchedule() {
		if(!canRun() || !canInit()) {
			this.cancel();
			return;
		}
		if(animation().isPresent() && animation().get().instant) {
			getMob().animate(animation().get().animation);
		}
		init();
	}

	@Override
	protected final void execute() {
		if(!canRun()) {
			cancel();
			return;
		}

		if(animation().isPresent() && animationCounter++ > animation().get().delay) {
			getMob().animate(animation().get().animation);
			animationCounter = 0;
		}

		onExecute();
	}

	/**
	 * A simple wrapper class which wraps an animation to a delay.
	 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
	 * @since 19-12-2016.
	 */
	public final class SkillAnimation {

		/**
		 * The animation for this set.
		 */
		public final Animation animation;

		/**
		 * The delay for this set.
		 */
		public final int delay;

		/**
		 * Determines if this animation should run when the task is submitted.
		 */
		public final boolean instant;

		/**
		 * Constructs a new {@link SkillAnimation};
		 * @param animation {@link #animation}.
		 * @param delay {@link #delay}.
		 * @param instant {@link #instant}.
		 */
		public SkillAnimation(Animation animation, int delay, boolean instant) {
			this.animation = animation;
			this.delay = delay;
			this.instant = instant;
		}

		/**
		 * Constructs a new {@link SkillAnimation};
		 * @param animation {@link #animation}.
		 * @param delay {@link #delay}.
		 */
		public SkillAnimation(Animation animation, int delay) {
			this(animation, delay, true);
		}
	}
}
