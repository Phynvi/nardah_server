package com.nardah.game.event.impl;

import com.nardah.game.world.items.Item;
import com.nardah.game.event.Event;
import com.nardah.game.world.entity.actor.mob.Mob;

public class ItemOnNpcEvent implements Event {

	private final Mob mob;
	private final Item used;
	private final int slot;

	public ItemOnNpcEvent(Mob mob, Item used, int slot) {
		this.mob = mob;
		this.used = used;
		this.slot = slot;
	}

	public Mob getMob() {
		return mob;
	}

	public Item getUsed() {
		return used;
	}

	public int getSlot() {
		return slot;
	}

}
