package com.nardah.content.skill.impl.hunter.trap;

import com.nardah.content.skill.SkillAction;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.game.world.object.CustomGameObject;
import com.nardah.game.world.pathfinding.TraversalMap;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.region.Region;
import com.nardah.net.packet.out.SendMessage;

import java.util.Optional;

/**
 * @author Ethan Kyle Millard <skype:pumpklins>
 * @since Tue, August 07, 2018 @ 7:35 PM
 */
public class BoxTrapping extends SkillAction {

	private Player player;
	private final Traps trap;

	public BoxTrapping(Player player, Traps trap) {
		super(player, Optional.empty(), false);
		this.player = player;
		this.trap = trap;
	}

	@Override
	public boolean canInit() {
		int levelRequirement = trap.getBaseLevel(), currentLevel = player.skills.getLevel(Skill.HUNTER);

		if(currentLevel < levelRequirement) {
			player.send(new SendMessage("You need a Hunter level of " + levelRequirement + " in order to place this trap."));
			return false;
		} else {
			Region region = getMob().getRegion();
			if(region.containsObject(getMob().getPosition())) {
				getMob().getPlayer().send(new SendMessage("You can't place a trap here."));
				return false;
			}
			int maxAmount = getMaximumTrap(currentLevel);
			if(getTrapsCount() == maxAmount) {
				player.send(new SendMessage("You cannot place more than " + maxAmount + " traps at once."));
				return false;
			}
		}
		return true;
	}

	@Override
	public void init() {
		if(getMob().isPlayer()) {
			getMob().getPlayer().send(new SendMessage("You begin setting up the trap."));
		}
		getMob().getPlayer().locking.lock(1);
		getMob().animate(trap.getLayAnimation());
	}

	@Override
	public void onExecute() {
		Player player = (Player) getMob();
		player.inventory.remove(trap.getItems()[0]);
		Position walkTo = player.getPosition();

		if(TraversalMap.isTraversable(player.getPosition(), Direction.WEST, player.width())) {
			walkTo = walkTo.west();
		} else if(TraversalMap.isTraversable(player.getPosition(), Direction.EAST, player.width())) {
			walkTo = walkTo.east();
		} else if(TraversalMap.isTraversable(player.getPosition(), Direction.SOUTH, player.width())) {
			walkTo = walkTo.north();
		} else if(TraversalMap.isTraversable(player.getPosition(), Direction.NORTH, player.width())) {
			walkTo = walkTo.south();
		}

		if(!player.getPosition().equals(walkTo)) {
			player.movement.walkTo(walkTo);
		}

		//        RandomEventHandler.trigger(player);
		CustomGameObject object = new CustomGameObject(trap.getObjectId(), player.getPosition());
		object.register();

		World.schedule(new Task(180) {
			@Override
			protected void execute() {
				GroundItem.create(player, new Item(trap.getItems()[0].getId()), object.getPosition());
				object.unregister();
				cancel();
			}
		});
		cancel();
	}

	@Override
	public Optional<SkillAnimation> animation() {
		return Optional.empty();
	}

	@Override
	public double experience() {
		return 0;
	}

	@Override
	public int skill() {
		return Skill.HUNTER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public String getName() {
		return "Trap hunter";
	}

	/**
	 * Calculates the maximum number of traps the player can set.
	 * @param currentLevel The current hunter level.
	 * @return The trap capacity based on the current level.
	 */
	private int getMaximumTrap(int currentLevel) {
		return 1 + (currentLevel / 20);
	}

	/**
	 * @return the number of laid traps by the player.
	 */
	private int getTrapsCount() {
		int count = 0;
		for(Traps trap : Traps.values())
			count += Region.getActiveObjectsForId(player, trap.getObjectId());
		return count;
	}
}
