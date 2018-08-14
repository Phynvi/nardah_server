package io.battlerune.game.event.impl;

import io.battlerune.game.event.Event;
import io.battlerune.game.world.object.GameObject;

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
