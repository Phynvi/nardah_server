package com.nardah.game.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.nardah.Config;
import com.nardah.game.world.World;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public final class WebsitePlayerCountService extends AbstractScheduledService {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.SYSTEM);
	
	private static final WebsitePlayerCountService INSTANCE = new WebsitePlayerCountService();
	
	public static WebsitePlayerCountService getInstance() {
		return INSTANCE;
	}
	
	@Override
	protected void runOneIteration() throws Exception {
		try {
			final int count = World.getPlayerCount();
			InputStream is = new URL(String.format("%s/player_count.php?key=9A@2U0764JqB&amount=%d", Config.WEBSITE_URL, count)).openStream();
			is.close();
		} catch(IOException ex) {
			logger.error("Error writing player count on website.");
			ex.printStackTrace();
		}
	}
	
	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedRateSchedule(10, 30, TimeUnit.SECONDS);
	}
	
}
