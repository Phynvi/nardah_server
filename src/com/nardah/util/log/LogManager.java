package com.nardah.util.log;

public class LogManager {
	
	private static final Logger loggers[];
	
	static {
		LoggerType[] types = LoggerType.values();
		loggers = new Logger[types.length];
		for(LoggerType t : types) {
			loggers[t.ordinal()] = new Logger(t);
		}
	}
	
	public static Logger getLogger(LoggerType type) {
		return loggers[type.ordinal()];
	}
}
