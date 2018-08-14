package io.battlerune.game.event.impl;

import io.battlerune.game.event.Event;
import io.battlerune.game.world.entity.actor.player.command.CommandParser;

public class CommandEvent implements Event {

	private final CommandParser parser;

	public CommandEvent(CommandParser parser) {
		this.parser = parser;
	}

	public CommandParser getParser() {
		return parser;
	}

}
