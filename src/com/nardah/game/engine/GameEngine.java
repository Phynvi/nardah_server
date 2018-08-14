package com.nardah.game.engine;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.nardah.game.engine.sync.ClientSynchronizer;
import com.nardah.game.engine.sync.ParallelClientSynchronizer;
import com.nardah.game.engine.sync.SequentialClientSynchronizer;
import com.nardah.util.Stopwatch;
import com.nardah.Config;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.MobList;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.session.GameSession;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.util.concurrent.TimeUnit;

public final class GameEngine extends AbstractScheduledService {
	
	/**
	 * Logger.
	 */
	private static final Logger logger = LogManager.getLogger(LoggerType.UPDATING);
	
	/**
	 * Synchronizer.
	 */
	private final ClientSynchronizer synchronizer;
	
	@Override
	protected String serviceName() {
		return "GameThread";
	}
	
	public GameEngine() {
		synchronizer = (Config.PARALLEL_GAME_ENGINE ? new ParallelClientSynchronizer() : new SequentialClientSynchronizer());
	}
	
	@Override
	protected void runOneIteration() {
		final Stopwatch stopwatch = Stopwatch.start();
		final Stopwatch stopwatch2 = Stopwatch.start();
		
		World world = World.get();
		MobList<Player> players = World.getPlayers();
		MobList<Mob> npcs = World.getNpcs();
		
		world.dequeLogins();
		
		long elapsed = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
		
		if(elapsed > 10 && Config.SERVER_DEBUG) {
			System.out.println(String.format("world.dequeLogins(): %d ms", elapsed));
		}
		stopwatch.reset();
		
		world.dequeLogouts();
		
		elapsed = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
		
		if(elapsed > 10 && Config.SERVER_DEBUG) {
			System.out.println(String.format("world.dequeLogouts(): %d ms", elapsed));
		}
		stopwatch.reset();
		
		world.process();
		
		elapsed = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
		
		if(elapsed > 10 && Config.SERVER_DEBUG) {
			System.out.println(String.format("world.process(): %d ms", elapsed));
		}
		stopwatch.reset();
		
		players.forEach(player -> {
			player.getSession().ifPresent(GameSession::processClientPackets);
			try {
				player.sequence();
			} catch(Exception ex) {
				logger.error(String.format("error player.sequence(): %s ms", player));
				ex.printStackTrace();
			}
		});
		
		elapsed = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
		
		if(elapsed > 10 && Config.SERVER_DEBUG) {
			System.out.println(String.format("player.sequence(): %d ms", elapsed));
		}
		stopwatch.reset();
		
		npcs.forEach(Mob::sequence);
		
		elapsed = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
		
		if(elapsed > 10 && Config.SERVER_DEBUG) {
			System.out.println(String.format("mob.sequence(): %d ms", elapsed));
		}
		stopwatch.reset();
		
		try {
			synchronizer.synchronize(players, npcs);
		} catch(Exception ex) {
			logger.error("Error in the main game sequencer.");
			ex.printStackTrace();
		}
		
		elapsed = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
		
		if(elapsed > 10 && Config.SERVER_DEBUG) {
			System.out.println(String.format("synchronizer.synchronize(): %d ms", elapsed));
		}
		
		stopwatch.reset();
		
		if(stopwatch2.elapsedTime(TimeUnit.MILLISECONDS) > 60 && Config.SERVER_DEBUG) {
			System.out.println(String.format("CYCLE END: %d ms", stopwatch2.elapsedTime(TimeUnit.MILLISECONDS)));
		}
		
	}
	
	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(600, 600, TimeUnit.MILLISECONDS);
	}
	
}
