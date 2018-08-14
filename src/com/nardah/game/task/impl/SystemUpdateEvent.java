package com.nardah.game.task.impl;

import com.nardah.game.world.World;
import com.nardah.util.Stopwatch;
import com.nardah.game.task.Task;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.util.concurrent.TimeUnit;

public final class SystemUpdateEvent extends Task {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.SYSTEM);
	
	private final boolean restart;
	
	private final int seconds;
	
	private final Stopwatch stopwatch = Stopwatch.start();
	
	private long temp;
	
	public SystemUpdateEvent(int seconds, boolean restart) {
		super(true, 0);
		this.seconds = seconds;
		this.restart = restart;
	}
	
	@Override
	public void execute() {
		final long ds = seconds - stopwatch.elapsedTime(TimeUnit.SECONDS) - 1;
		
		if(temp != ds && ds >= 0) {
			logger.info(String.format("Restarting in: %d seconds.", ds));
		}
		
		if(stopwatch.elapsed(seconds, TimeUnit.SECONDS)) {
			cancel();
		}
		
		temp = ds;
	}
	
	@Override
	protected void onCancel(boolean logout) {
		if(restart) {
			World.restart();
		} else {
			World.shutdown();
		}
	}
	
}
