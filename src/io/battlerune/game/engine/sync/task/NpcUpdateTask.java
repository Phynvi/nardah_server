package io.battlerune.game.engine.sync.task;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendNpcUpdate;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

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
