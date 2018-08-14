package io.battlerune.util.log;

public class Logger {
	
	private static final String INFO_PREFIX = "INFO: ";
	private static final String ERROR_PREFIX = "ERROR: ";
	private static final String WARNING_PREFIX = "WARNING: ";
	private static final String CONFIG_PREFIX = "CONFIG: ";
	
	private final LoggerType type;
	private final String prefix;
	
	public Logger(LoggerType type) {
		this.type = type;
		prefix = "[" + type.name() + "] ";
	}
	
	public void info(String msg) {
		System.out.print(prefix);
		System.out.print(INFO_PREFIX);
		System.out.println(msg);
	}
	
	public void error(String msg) {
		System.out.print(prefix);
		System.out.print(ERROR_PREFIX);
		System.out.println(msg);
	}
	
	public void warn(String msg) {
		System.out.print(prefix);
		System.out.print(WARNING_PREFIX);
		System.out.println(msg);
	}
	
	public void config(String msg) {
		System.out.print(prefix);
		System.out.print(CONFIG_PREFIX);
		System.out.println(msg);
	}
}
