package io.battlerune.content.activity.impl.flowerpoker;

import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.task.TaskManager;
import io.battlerune.game.task.impl.ObjectPlacementEvent;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.game.world.object.ObjectDirection;
import io.battlerune.game.world.object.ObjectType;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;
import io.battlerune.game.world.region.RegionManager;

import java.util.Random;

/**
 * Handles the flower clicking
 * @author Harryl / Nerik#8690
 */
public class FlowerHandler {

	private Player player;
	private Random random = new Random();
	private FlowerData[] flower = FlowerData.values();
	private FlowerData tempFlower;

	public FlowerHandler(Player player) {
		this.player = player;
	}

	public FlowerData getTempFlower() {
		return tempFlower;
	}

	public void setTempFlower(FlowerData tempFlower) {
		this.tempFlower = tempFlower;
	}

	public void plantFlower() {
		setTempFlower(getFlower());

		if(onFlower(player)) {
			player.message("You can't plant a flower on another flower!");
			return;
		}

		TaskManager.schedule(new Task(true, 1) {
			int tick = 0;

			@Override
			protected void execute() {
				switch(tick) {
					case 0:
						player.animate(new Animation(827));
						CustomGameObject gameObject = new CustomGameObject(getTempFlower().getObjectId(), player.getPosition().copy(), ObjectDirection.valueOf(0).orElse(ObjectDirection.WEST), ObjectType.INTERACTABLE);
						World.schedule(new ObjectPlacementEvent(gameObject, 50));
						player.message("You have planted " + ItemDefinition.get(getTempFlower().getItemId()).getName());
						break;
					case 1:
						player.walkExactlyTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY(), player.getPosition().getHeight()));
						break;
				}
				tick++;
			}
		});
	}

	private boolean onFlower(Actor actor) {
		for(Region region : RegionManager.getSurroundingRegions(actor.getPosition())) {
			for(GameObject object : region.getGameObjects(actor.getPosition())) {
				if(actor.getPosition().equals(object.getPosition())) {
					return true;
				}
			}
		}
		return false;
	}

	private FlowerData getFlower() {
		return flower[random.nextInt(flower.length)];
	}
}
