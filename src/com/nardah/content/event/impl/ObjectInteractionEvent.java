package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;
import com.nardah.game.world.object.GameObject;

public class ObjectInteractionEvent extends InteractionEvent {

	private final GameObject object;
	private final int opcode;

	public ObjectInteractionEvent(InteractionType type, GameObject object, int opcode) {
		super(type);
		this.object = object;
		this.opcode = opcode;
	}

	public GameObject getObject() {
		return object;
	}

	public int getOpcode() {
		return opcode;
	}

}