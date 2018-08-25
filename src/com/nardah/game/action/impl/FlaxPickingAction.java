package com.nardah.game.action.impl;

import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.task.impl.ObjectReplacementEvent;
import com.nardah.game.world.World;
import com.nardah.util.Utility;
import com.nardah.Config;
import com.nardah.game.Animation;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.object.GameObject;

/**
 * Handles picking up a flax.
 * @author Daniel
 */
public final class FlaxPickingAction extends Action<Player> {

	/** The flax game object. */
	private final GameObject object;

	/** The ticks. */
	private boolean pickup;

	/**
	 * Constructs a new <code>FlaxPickingAction</code>.
	 *
	 * @param player The player instance.
	 * @param object The flax game object.
	 */
	public FlaxPickingAction(Player player, GameObject object) {
		super(player, 2, true);
		this.object = object;
	}

	@Override
	public void execute() {
		Player player = getMob().getPlayer();

		if (pickup) {
			player.inventory.add(new Item(1779, 1));
			player.skills.addExperience(Skill.CRAFTING, 7 * Config.CRAFTING_MODIFICATION);
			//		if (Utility.random(6) == 1) {
			//		}
			cancel();
		} else {
			player.animate(new Animation(827));
			pickup = true;

			setDelay(2);
		}
		World.schedule(new ObjectReplacementEvent(object, 5));

	}

	@Override
	public String getName() {
		return "Flax picking";
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