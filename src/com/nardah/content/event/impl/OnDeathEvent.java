package com.nardah.content.event.impl;

import com.nardah.content.event.InteractionEvent;

/**
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 16-6-2017.
 */
public final class OnDeathEvent extends InteractionEvent {

	public OnDeathEvent() {
		super(InteractionType.ON_DEATH);
	}
}
