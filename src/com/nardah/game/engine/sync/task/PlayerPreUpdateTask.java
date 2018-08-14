package com.nardah.game.engine.sync.task;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

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
