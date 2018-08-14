package com.nardah.game.engine.sync.task;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

public class ParallelPlayerPreUpdateTask extends SynchronizationTask {

	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);

	private final Player player;

	public ParallelPlayerPreUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {

		} catch(Exception ex) {
			logger.error(String.format("Error in %s. player=%s", PlayerPreUpdateTask.class.getSimpleName(), player));
			ex.printStackTrace();
		}
	}

}
