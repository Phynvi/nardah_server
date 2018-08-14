package io.battlerune.game.engine.sync.task;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

public final class PlayerPreUpdateTask extends SynchronizationTask {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	private final Player player;
	
	public PlayerPreUpdateTask(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		try {
			try {
				player.movement.processNextMovement();
			} catch(Exception ex) {
				logger.error(String.format("error player.movement.processNextMovement() for %s", player));
				ex.printStackTrace();
			}
		} catch(Exception ex) {
			logger.error(String.format("Error in %s.", PlayerPreUpdateTask.class.getSimpleName()));
			ex.printStackTrace();
		}
	}
	
}
