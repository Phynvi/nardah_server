package io.battlerune.io;

import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.PacketRepository;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.util.Arrays;

/**
 * The class that loads all packet listeners.
 * @author nshusa
 */
public final class PacketListenerLoader implements Runnable {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.NETWORKING);
	
	@Override
	public void run() {
		new FastClasspathScanner().matchClassesImplementing(PacketListener.class, subclass -> {
			PacketListenerMeta annotation = subclass.getAnnotation(PacketListenerMeta.class);
			try {
				PacketListener listener = subclass.newInstance();
				Arrays.stream(annotation.value()).forEach(it -> PacketRepository.registerListener(it, listener));
			} catch(InstantiationException | IllegalAccessException ex) {
				logger.error("error loading packet listeners.");
				ex.printStackTrace();
			}
		}).scan();
	}
	
}
