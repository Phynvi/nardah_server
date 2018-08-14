package io.battlerune.util.sql;

import io.battlerune.util.sql.impl.MySqlCreateTableCommand;
import io.battlerune.util.sql.impl.MySqlInsertCommand;

import java.util.HashMap;
import java.util.Map;

public class MySqlCommandManager {
	
	public static Map<MySqlCommands, MySqlCommandListener> commands = new HashMap<>();
	
	public static void load() {
		
		commands.put(MySqlCommands.INSERT, new MySqlInsertCommand());
		commands.put(MySqlCommands.CREATE_TABLE, new MySqlCreateTableCommand());
	}
}
