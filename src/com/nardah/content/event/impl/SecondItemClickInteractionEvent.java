package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;
import com.nardah.game.world.items.Item;

public class SecondItemClickInteractionEvent extends ItemInteractionEvent {

	public SecondItemClickInteractionEvent(Item item, int slot) {
		super(InteractionEvent.InteractionType.SECOND_ITEM_CLICK, item, slot, 1);
	}
}
