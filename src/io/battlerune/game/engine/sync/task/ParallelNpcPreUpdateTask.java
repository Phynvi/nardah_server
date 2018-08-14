package io.battlerune.game.engine.sync.task;

import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

public class ParallelNpcPreUpdateTask extends SynchronizationTask {

	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);

	private final Npc npc;

	public ParallelNpcPreUpdateTask(Npc npc) {
		this.npc = npc;
	}

	@Override
	public void run() {
		try {
			if(npc.atomicPlayerCount.get() == 0) {
				return;
			}

			if(npc.regionChange) {
				npc.lastPosition = npc.getPosition();
			}

			npc.movement.processNextMovement();
			npc.sequence();
		} catch(Exception ex) {
			logger.error(String.format("Error in %s. npc=%d %s", NpcPreUpdateTask.class.getSimpleName(), npc.id, npc.getPosition()));
			ex.printStackTrace();
		}
	}

}
