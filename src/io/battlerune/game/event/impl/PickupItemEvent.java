package io.battlerune.game.event.impl;

import io.battlerune.game.event.Event;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.ground.GroundItem;
import io.battlerune.game.world.position.Position;

public class PickupItemEvent implements Event {

	private final GroundItem groundItem;

	public PickupItemEvent(GroundItem groundItem) {
		this.groundItem = groundItem;
	}

	public GroundItem getGroundItem() {
		return groundItem;
	}

	public Item getItem() {
		return groundItem.item;
	}

	public Position getPosition() {
		return groundItem.getPosition();
	}

}
