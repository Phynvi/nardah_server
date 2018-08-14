package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;
import com.nardah.game.world.entity.actor.Actor;

/**
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 16-6-2017.
 */
public final class OnKillEvent extends InteractionEvent {

	private final Actor victim;

	public OnKillEvent(Actor victim) {
		super(InteractionType.ON_KILL);
		this.victim = victim;
	}

	public Actor getVictim() {
		return victim;
	}
}
