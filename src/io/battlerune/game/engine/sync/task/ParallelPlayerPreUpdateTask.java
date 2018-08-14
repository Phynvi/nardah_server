package io.battlerune.game.engine.sync.task;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

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
