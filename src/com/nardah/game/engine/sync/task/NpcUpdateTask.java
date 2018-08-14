package com.nardah.game.engine.sync.task;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendNpcUpdate;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

public class NpcUpdateTask extends SynchronizationTask {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	private final Player player;
	
	public NpcUpdateTask(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		try {
			if(player == null) {
				return;
			}
			
			player.send(new SendNpcUpdate());
		} catch(Exception ex) {
			logger.error(String.format("Error in %s %s", this.getClass().getSimpleName(), player));
			ex.printStackTrace();
		}
	}
	
}
