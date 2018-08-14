package com.nardah.game.event.impl;

import com.nardah.game.world.position.Position;
import com.nardah.game.event.Event;

public class MovementEvent implements Event {
	
	private final Position destination;
	
	public MovementEvent(Position destination) {
		this.destination = destination;
	}
	
	public Position getDestination() {
		return destination;
	}
	
}
