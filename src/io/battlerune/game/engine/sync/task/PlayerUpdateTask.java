package io.battlerune.game.engine.sync.task;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendPlayerUpdate;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

public final class PlayerUpdateTask extends SynchronizationTask {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	private final Player player;
	
	public PlayerUpdateTask(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		try {
			if(player == null) {
				return;
			}
			
			player.send(new SendPlayerUpdate());
		} catch(Exception ex) {
			logger.error(String.format("Error in %s %s", PlayerUpdateTask.class.getSimpleName(), player));
			ex.printStackTrace();
		}
	}
	
}
