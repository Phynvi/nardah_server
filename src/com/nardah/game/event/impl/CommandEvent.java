package com.nardah.game.event.impl;

import com.nardah.game.world.entity.actor.player.command.CommandParser;
import com.nardah.game.event.Event;

public class CommandEvent implements Event {

	private final CommandParser parser;

	public CommandEvent(CommandParser parser) {
		this.parser = parser;
	}

	public CommandParser getParser() {
		return parser;
	}

}
