package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;
import com.nardah.game.world.items.Item;

public class ItemInteractionEvent extends InteractionEvent {

	private final Item item;
	private final int slot, opcode;

	ItemInteractionEvent(InteractionType type, Item item, int slot, int opcode) {
		super(type);
		this.item = item;
		this.slot = slot;
		this.opcode = opcode;
	}

	public Item getItem() {
		return item;
	}

	public int getOpcode() {
		return opcode;
	}

	public int getSlot() {
		return slot;
	}

}