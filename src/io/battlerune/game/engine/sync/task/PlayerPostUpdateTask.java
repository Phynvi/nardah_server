package io.battlerune.game.engine.sync.task;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.session.GameSession;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

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
