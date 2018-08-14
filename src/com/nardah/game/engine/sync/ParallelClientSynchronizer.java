package com.nardah.game.engine.sync;

import com.nardah.game.engine.sync.task.*;
import io.battlerune.game.engine.sync.task.*;
import com.nardah.game.world.entity.MobList;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public final class ParallelClientSynchronizer implements ClientSynchronizer {

	private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final Phaser phaser = new Phaser(1);

	@Override
	public void synchronize(MobList<Player> players, MobList<Mob> npcs) {

		// player movement
		phaser.bulkRegister(players.size());
		players.forEach(player -> executor.submit(new PhasedUpdateTask(phaser, new PlayerPreUpdateTask(player))));
		phaser.arriveAndAwaitAdvance();

		// mob movement
		phaser.bulkRegister(npcs.size());
		npcs.forEach(npc -> executor.submit(new PhasedUpdateTask(phaser, new NpcPreUpdateTask(npc))));
		phaser.arriveAndAwaitAdvance();

		// player updating
		phaser.bulkRegister(players.size());
		players.forEach(player -> executor.submit(new PhasedUpdateTask(phaser, new PlayerUpdateTask(player))));
		phaser.arriveAndAwaitAdvance();

		// mob updating
		phaser.bulkRegister(players.size());
		players.forEach(player -> executor.submit(new PhasedUpdateTask(phaser, new NpcUpdateTask(player))));
		phaser.arriveAndAwaitAdvance();

		// reset player
		phaser.bulkRegister(players.size());
		players.forEach(player -> executor.submit(new PhasedUpdateTask(phaser, new PlayerPostUpdateTask(player))));
		phaser.arriveAndAwaitAdvance();

		// reset mob
		phaser.bulkRegister(npcs.size());
		npcs.forEach(npc -> executor.submit(new PhasedUpdateTask(phaser, new NpcPostUpdateTask(npc))));
		phaser.arriveAndAwaitAdvance();

	}

}
