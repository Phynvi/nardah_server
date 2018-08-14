package io.battlerune.game.event.impl.log;

import io.battlerune.Config;
import io.battlerune.game.event.Event;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

import java.time.LocalDateTime;

public abstract class LogEvent implements Event {

	private static final Logger logger = LogManager.getLogger(LoggerType.CONTENT);
	protected final LocalDateTime dateTime = LocalDateTime.now();

	public void log() {
		if(!Config.FORUM_INTEGRATION || !Config.LOG_PLAYER || true) {
			return;
		}

		new Thread(() -> {
			try {
				onLog();
			} catch(Exception ex) {
				//logger.error(String.format("Error logging %s", this.getClass().getSimpleName()), ex);
			}
		}).start();
	}

	public abstract void onLog() throws Exception;

}
