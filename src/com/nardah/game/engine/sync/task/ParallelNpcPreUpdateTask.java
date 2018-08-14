package com.nardah.game.engine.sync.task;

import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

public class ParallelNpcPreUpdateTask extends SynchronizationTask {

	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);

	private final Mob mob;

	public ParallelNpcPreUpdateTask(Mob mob) {
		this.mob = mob;
	}

	@Override
	public void run() {
		try {
			if(mob.atomicPlayerCount.get() == 0) {
				return;
			}

			if(mob.regionChange) {
				mob.lastPosition = mob.getPosition();
			}

			mob.movement.processNextMovement();
			mob.sequence();
		} catch(Exception ex) {
			logger.error(String.format("Error in %s. mob=%d %s", NpcPreUpdateTask.class.getSimpleName(), mob.id, mob.getPosition()));
			ex.printStackTrace();
		}
	}

}
