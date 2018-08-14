package io.battlerune.game.event.impl;

import io.battlerune.game.event.Event;
import io.battlerune.game.world.items.Item;

public class ItemClickEvent implements Event {

	private final int type;

	private final Item item;

	private final int slot;

	public ItemClickEvent(int type, Item item, int slot) {
		this.type = type;
		this.item = item;
		this.slot = slot;
	}

	public int getType() {
		return type;
	}

	public Item getItem() {
		return item;
	}

	public int getSlot() {
		return slot;
	}

}
