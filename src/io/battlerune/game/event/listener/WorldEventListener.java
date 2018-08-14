package io.battlerune.game.event.listener;

import io.battlerune.game.event.Event;
import io.battlerune.game.event.impl.log.LogEvent;

public final class WorldEventListener implements EventListener {
	
	@Override
	public void accept(Event event) {
		if(event instanceof LogEvent) {
			LogEvent logEvent = (LogEvent) event;
			logEvent.log();
		}
	}
	
}
