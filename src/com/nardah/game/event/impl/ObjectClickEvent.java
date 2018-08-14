package com.nardah.game.event.impl;

import com.nardah.game.world.object.GameObject;
import com.nardah.game.event.Event;

public class ObjectClickEvent implements Event {

	private final int type;

	private final GameObject object;

	public ObjectClickEvent(int type, GameObject object) {
		this.type = type;
		this.object = object;
	}

	public int getType() {
		return type;
	}

	public GameObject getObject() {
		return object;
	}

}
