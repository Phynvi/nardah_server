package com.nardah.content.event.impl;

import com.nardah.game.world.object.GameObject;

public class ThirdObjectClick extends ObjectInteractionEvent {

	public ThirdObjectClick(GameObject object) {
		super(InteractionType.THIRD_CLICK_OBJECT, object, 2);
	}

}