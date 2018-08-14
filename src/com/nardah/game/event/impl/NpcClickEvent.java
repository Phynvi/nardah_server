package com.nardah.game.event.impl;

import com.nardah.game.event.Event;
import com.nardah.game.world.entity.actor.mob.Mob;

public class NpcClickEvent implements Event {

	private final int type;

	private final Mob mob;

	public NpcClickEvent(int type, Mob mob) {
		this.type = type;
		this.mob = mob;
	}

	public int getType() {
		return type;
	}

	public Mob getMob() {
		return mob;
	}

}
