package com.nardah.game.engine.sync.task;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendPlayerUpdate;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

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
