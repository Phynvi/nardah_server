package io.battlerune.util.sql;

public class MySqlLogHandler {
	
	public static void run(MySqlCommands command, String log) {
		MySqlCommandListener plugin = MySqlCommandManager.commands.get(command);
		if(plugin != null) {
			plugin.execute(log);
		}
	}
}
