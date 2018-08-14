package com.nardah.game.engine.sync.task;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.session.GameSession;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

public final class PlayerPostUpdateTask extends SynchronizationTask {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	private final Player player;
	
	public PlayerPostUpdateTask(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		try {
			player.viewport.calculateViewingDistance();
			player.updateFlags.clear();
			player.resetAnimation();
			player.resetGraphic();
			player.clearTeleportTarget();
			player.positionChange = false;
			player.regionChange = false;
			player.teleportRegion = false;
			player.facePosition = null;
			player.getSession().ifPresent(GameSession::processServerPacketQueue);
		} catch(Exception ex) {
			logger.error(String.format("Error in %s", PlayerPostUpdateTask.class.getSimpleName()));
			ex.printStackTrace();
		}
	}
	
}
