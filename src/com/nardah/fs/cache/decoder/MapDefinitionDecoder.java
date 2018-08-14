package com.nardah.fs.cache.decoder;

import com.nardah.game.world.region.RegionDefinition;
import com.nardah.fs.cache.FileSystem;
import com.nardah.fs.cache.archive.Archive;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.nio.ByteBuffer;

/**
 * A class which parses {@link RegionDefinition}s
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public final class MapDefinitionDecoder implements Runnable {
	
	/**
	 * The IndexedFileSystem.
	 */
	private final FileSystem fs;
	
	/**
	 * Logger.
	 */
	private final Logger logger = LogManager.getLogger(LoggerType.START_UP);
	
	/**
	 * Creates the {@link MapDefinitionDecoder}.
	 * @param fs The {@link FileSystem}.
	 */
	public MapDefinitionDecoder(FileSystem fs) {
		this.fs = fs;
	}
	
	@Override
	public void run() {
		logger.info("Loading region definitions");
		Archive archive = fs.getArchive(FileSystem.MANIFEST_ARCHIVE);
		ByteBuffer buffer = archive.getData("map_index");
		int count = buffer.getShort() & 0xFFFF;
		for(int i = 0; i < count; i++) {
			int hash = buffer.getShort() & 0xFFFF;
			int terrainFile = buffer.getShort() & 0xFFFF;
			int objectFile = buffer.getShort() & 0xFFFF;
			RegionDefinition.set(new RegionDefinition(hash, terrainFile, objectFile));
		}
		logger.info("Loaded " + count + " region definitions");
	}
	
}