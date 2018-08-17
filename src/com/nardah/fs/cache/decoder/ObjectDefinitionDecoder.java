package com.nardah.fs.cache.decoder;

import com.nardah.game.world.object.ObjectDefinition;
import com.nardah.fs.cache.FileSystem;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

public final class ObjectDefinitionDecoder implements Runnable {
	
	private final static Logger LOGGER = LogManager.getLogger(LoggerType.START_UP);
	
	private final FileSystem fs;
	
	public ObjectDefinitionDecoder(FileSystem fs) {
		this.fs = fs;
	}
	
	@Override
	public void run() {
		LOGGER.info("Loading object definitions.");
		ObjectDefinition.init(fs.getArchive(FileSystem.CONFIG_ARCHIVE), LOGGER);
	}
	
}
