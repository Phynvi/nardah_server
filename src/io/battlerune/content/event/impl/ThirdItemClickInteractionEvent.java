package io.battlerune.content.event.impl;

import io.battlerune.game.world.items.Item;

public class ThirdItemClickInteractionEvent extends ItemInteractionEvent {

	public ThirdItemClickInteractionEvent(Item item, int slot) {
		super(InteractionType.THIRD_ITEM_CLICK, item, slot, 2);
	}
}
