package io.battlerune.game.engine.sync.task;

import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

public final class NpcPostUpdateTask extends SynchronizationTask {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	private final Npc npc;
	
	public NpcPostUpdateTask(Npc npc) {
		this.npc = npc;
	}
	
	@Override
	public void run() {
		try {
			npc.updateFlags.clear();
			npc.resetAnimation();
			npc.resetGraphic();
			npc.clearTeleportTarget();
			npc.positionChange = false;
			npc.regionChange = false;
			npc.teleportRegion = false;
		} catch(Exception ex) {
			logger.error(String.format("Error in %s", NpcPostUpdateTask.class.getSimpleName()));
			ex.printStackTrace();
		}
	}
	
}
