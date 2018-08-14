package io.battlerune.content.event.impl;

import io.battlerune.content.event.InteractionEvent;
import io.battlerune.game.world.items.Item;

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