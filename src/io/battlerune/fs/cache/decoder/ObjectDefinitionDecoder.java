package io.battlerune.fs.cache.decoder;

import io.battlerune.fs.cache.FileSystem;
import io.battlerune.game.world.object.ObjectDefinition;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

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
