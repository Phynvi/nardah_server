package com.nardah.net.packet;

import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;
import com.nardah.net.packet.PacketRepository;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;
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
