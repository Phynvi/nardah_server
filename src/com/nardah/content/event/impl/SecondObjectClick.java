package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;
import com.nardah.game.world.object.GameObject;

public class SecondObjectClick extends ObjectInteractionEvent {

	public SecondObjectClick(GameObject object) {
		super(InteractionEvent.InteractionType.SECOND_CLICK_OBJECT, object, 1);
	}

}