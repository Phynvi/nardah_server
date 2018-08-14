package com.nardah.game.event.impl;

import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Position;
import com.nardah.game.event.Event;

public class DropItemEvent implements Event {

	private final Item item;
	private final int slot;
	private final Position position;

	public DropItemEvent(Item item, int slot, Position position) {
		this.item = item;
		this.slot = slot;
		this.position = position;
	}

	public Item getItem() {
		return item;
	}

	public int getSlot() {
		return slot;
	}

	public Position getPosition() {
		return position;
	}

}
