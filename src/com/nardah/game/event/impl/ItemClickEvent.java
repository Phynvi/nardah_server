package com.nardah.game.event.impl;

import com.nardah.game.world.items.Item;
import com.nardah.game.event.Event;

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
