package com.nardah.game.engine.sync.task;

import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

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
