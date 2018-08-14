package com.nardah.game.event.impl;

import com.nardah.game.world.items.Item;
import com.nardah.game.event.Event;
import com.nardah.game.world.entity.actor.player.Player;

public class ItemOnPlayerEvent implements Event {

	private final Player other;

	private final Item used;

	private final int slot;

	public ItemOnPlayerEvent(Player other, Item used, int slot) {
		this.other = other;
		this.used = used;
		this.slot = slot;
	}

	public Player getOther() {
		return other;
	}

	public Item getUsed() {
		return used;
	}

	public int getSlot() {
		return slot;
	}

}
