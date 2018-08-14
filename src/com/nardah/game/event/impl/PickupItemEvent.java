package com.nardah.game.event.impl;

import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.game.world.position.Position;
import com.nardah.game.event.Event;

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
