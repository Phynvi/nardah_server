package io.battlerune.game.event.impl;

import io.battlerune.game.event.Event;
import io.battlerune.game.world.position.Position;

public class MovementEvent implements Event {
	
	private final Position destination;
	
	public MovementEvent(Position destination) {
		this.destination = destination;
	}
	
	public Position getDestination() {
		return destination;
	}
	
}
