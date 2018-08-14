package io.battlerune.game.engine.sync.task;

import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

import java.util.concurrent.Phaser;

public final class PhasedUpdateTask extends SynchronizationTask {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	private final SynchronizationTask task;
	private final Phaser phaser;
	
	public PhasedUpdateTask(Phaser phaser, SynchronizationTask task) {
		this.task = task;
		this.phaser = phaser;
	}
	
	@Override
	public void run() {
		try {
			task.run();
		} catch(Exception ex) {
			logger.error(String.format("Error in %s", task.getClass().getSimpleName()));
			ex.printStackTrace();
		}
		phaser.arriveAndDeregister();
	}
	
}
