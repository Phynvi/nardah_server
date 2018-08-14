package io.battlerune.content.event.impl;

import io.battlerune.game.world.object.GameObject;

public class FirstObjectClick extends ObjectInteractionEvent {

	public FirstObjectClick(GameObject object) {
		super(InteractionType.FIRST_CLICK_OBJECT, object, 0);
	}

}