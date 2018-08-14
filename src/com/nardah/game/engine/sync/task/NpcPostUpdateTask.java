package com.nardah.game.engine.sync.task;

import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

public final class NpcPostUpdateTask extends SynchronizationTask {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	private final Mob mob;
	
	public NpcPostUpdateTask(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	public void run() {
		try {
			mob.updateFlags.clear();
			mob.resetAnimation();
			mob.resetGraphic();
			mob.clearTeleportTarget();
			mob.positionChange = false;
			mob.regionChange = false;
			mob.teleportRegion = false;
		} catch(Exception ex) {
			logger.error(String.format("Error in %s", NpcPostUpdateTask.class.getSimpleName()));
			ex.printStackTrace();
		}
	}
	
}
