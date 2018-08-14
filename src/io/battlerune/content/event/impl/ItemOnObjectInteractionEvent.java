package io.battlerune.content.event.impl;

import io.battlerune.content.event.InteractionEvent;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.object.GameObject;

public class ItemOnObjectInteractionEvent extends InteractionEvent {

	private final Item item;
	private final GameObject object;

	public ItemOnObjectInteractionEvent(Item item, GameObject object) {
		super(InteractionType.ITEM_ON_OBJECT);
		this.item = item;
		this.object = object;
	}

	public Item getItem() {
		return item;
	}

	public GameObject getObject() {
		return object;
	}
}
