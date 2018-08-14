package com.nardah.game.event.listener;

import com.nardah.game.event.Event;
import com.nardah.game.event.impl.log.LogEvent;

public final class WorldEventListener implements EventListener {
	
	@Override
	public void accept(Event event) {
		if(event instanceof LogEvent) {
			LogEvent logEvent = (LogEvent) event;
			logEvent.log();
		}
	}
	
}
