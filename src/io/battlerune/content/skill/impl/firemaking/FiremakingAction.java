package io.battlerune.content.skill.impl.firemaking;

import io.battlerune.Config;
import io.battlerune.content.activity.randomevent.RandomEventHandler;
import io.battlerune.content.clanchannel.content.ClanTaskKey;
import io.battlerune.content.prestige.PrestigePerk;
import io.battlerune.content.skill.impl.DestructionSkillAction;
import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.ground.GroundItem;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.pathfinding.TraversalMap;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.RandomUtils;

import java.util.Optional;

/**
 * Handles the firemaking action.
 * @author Daniel
 */
public final class FiremakingAction extends DestructionSkillAction {
	private final Item item;
	private final FiremakingData firemaking;

	FiremakingAction(Player player, Item item, FiremakingData firemaking) {
		super(player, Optional.empty(), false);
		this.item = item;
		this.firemaking = firemaking;
	}

	@Override
	public boolean canInit() {
		if(getMob().skills.getMaxLevel(Skill.FIREMAKING) < firemaking.getLevel()) {
			getMob().getPlayer().message("You need a firemaking level of " + firemaking.getLevel() + " to light this log!");
			return false;
		}
		if(Area.inEdgeville(getMob()) || Area.inDonatorZone(getMob()) || Area.inWilderness(getMob())) {
			getMob().getPlayer().message("You can not burn a fire here!");
			return false;
		}
		Region region = getMob().getRegion();
		if(region.containsObject(getMob().getPosition())) {
			getMob().getPlayer().send(new SendMessage("You can't light a fire here!"));
			return false;
		}
		return true;
	}

	@Override
	public Optional<SkillAnimation> animation() {
		return Optional.empty();
	}

	@Override
	public double experience() {
		return firemaking.getExperience() * Config.FIREMAKING_MODIFICATION;
	}

	@Override
	public int skill() {
		return Skill.FIREMAKING;
	}

	@Override
	public void init() {
		if(getMob().isPlayer()) {
			getMob().getPlayer().send(new SendMessage("You attempt to light the log."));
		}
		getMob().animate(new Animation(733));
	}

	@Override
	public double successFactor() {
		return 100 - firemaking.getLevel();
	}

	@Override
	public void onDestruct(boolean success) {
		if(success) {
			Player player = (Player) getMob();
			player.inventory.remove(item);
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

			if(player.prestige.hasPerk(PrestigePerk.FLAME_ON) && RandomUtils.success(.25)) {
				player.inventory.remove(firemaking.getLog(), 1);
				player.skills.addExperience(Skill.FIREMAKING, experience());
			}

			RandomEventHandler.trigger(player);
			CustomGameObject object = new CustomGameObject(5249, player.getPosition());
			object.register();

			if(firemaking == FiremakingData.WILLOW_LOG) {
				player.forClan(channel -> channel.activateTask(ClanTaskKey.BURN_WILLOW_LOG, getMob().getName()));
			}

			World.schedule(new Task(180) {
				@Override
				protected void execute() {
					GroundItem.createGlobal(player, new Item(592), object.getPosition());
					object.unregister();
					cancel();
				}
			});
		}
	}

	@Override
	public void onCancel(boolean logout) {
		Player player = (Player) getMob();
		player.animate(new Animation(65535));
	}

	@Override
	public String getName() {
		return "Firemaking";
	}

	@Override
	public Item destructItem() {
		return item;
	}
}
