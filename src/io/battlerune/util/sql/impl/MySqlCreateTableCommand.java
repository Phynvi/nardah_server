package io.battlerune.util.sql.impl;

import io.battlerune.util.sql.MySqlCommandListener;
import io.battlerune.util.sql.MySqlConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Inserts the log of the player into the database
 * @author Nerik#8690
 */
public class MySqlCreateTableCommand implements MySqlCommandListener {

	@Override
	public void execute(String log) {
		try {
			Connection connection = DriverManager.getConnection(MySqlConnector.URL, MySqlConnector.USERNAME, MySqlConnector.PASSWORD);
			PreparedStatement table = connection.prepareStatement("CREATE TABLE IF NOT EXISTS logs(id int NOT NULL AUTO_INCREMENT, LOG varchar(255), DATE varchar(255), PRIMARY KEY(id))");
			table.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("[MySql Manager] Finished creating table!");
		}
	}

}
