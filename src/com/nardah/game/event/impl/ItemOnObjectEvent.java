package com.nardah.game.event.impl;

import com.nardah.game.world.items.Item;
import com.nardah.game.world.object.GameObject;
import com.nardah.game.event.Event;

public class ItemOnObjectEvent implements Event {

	private final Item used;

	private final int slot;

	private final GameObject object;

	public ItemOnObjectEvent(Item used, int slot, GameObject object) {
		this.used = used;
		this.slot = slot;
		this.object = object;
	}

	public Item getUsed() {
		return used;
	}

	public int getSlot() {
		return slot;
	}

	public GameObject getObject() {
		return object;
	}

}
