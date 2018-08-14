package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;
import com.nardah.game.world.items.Item;

public class ItemOnItemInteractionEvent extends InteractionEvent {

	private final Item first, second;
	private final int firstSlot, secondSlot;

	public ItemOnItemInteractionEvent(Item first, Item second, int firstSlot, int secondSlot) {
		super(InteractionType.ITEM_ON_ITEM);
		this.first = first;
		this.second = second;
		this.firstSlot = firstSlot;
		this.secondSlot = secondSlot;
	}

	public Item getFirst() {
		return first;
	}

	public int getFirstSlot() {
		return firstSlot;
	}

	public Item getSecond() {
		return second;
	}

	public int getSecondSlot() {
		return secondSlot;
	}
}
