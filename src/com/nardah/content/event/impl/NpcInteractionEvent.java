package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;
import com.nardah.game.world.entity.actor.mob.Mob;

public class NpcInteractionEvent extends InteractionEvent {

	private final Mob mob;
	private final int opcode;

	public NpcInteractionEvent(InteractionType type, Mob mob, int opcode) {
		super(type);
		this.mob = mob;
		this.opcode = opcode;
	}

	public Mob getMob() {
		return mob;
	}

	public int getOpcode() {
		return opcode;
	}

}
