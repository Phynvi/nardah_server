package com.nardah.util.database;


import com.nardah.util.database.query.SQLExecution;
import com.nardah.util.database.query.SQLQuery;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


/**
 * Manages connectivity with a MySQL Database server and transmits SQL Commands
 * to the databases
 * 
 * @author Telopya
 *
 */
public class MySQLDatabase {

	/**
	 * The name of this {@link MySQLDatabase}
	 */
	private final String name;

	/**
	 * 
	 */
	private final BasicDataSource source;

	/**
	 * Construct a new MySQL database
	 * 
	 * @param name
	 *            the database name
	 */
	public MySQLDatabase(String name) {
		this.name = name;
		this.source = new BasicDataSource();
	}

	/**
	 * Prepare the MySQL Database for connectivity and query handling
	 * 
	 * @param host
	 *            the MySQL Database host address
	 * @param user
	 *            the MySQL Database verification user
	 * @param password
	 *            the MySQL Database verification password
	 */
	public void prepare(String host, String user, String password) {

		try {
			source.setDriverClassName("com.mysql.jdbc.Driver");
			source.setUrl("jdbc:mysql://" + host + ":3306/" + name);
			source.setUsername(user);
			source.setPassword(password);
			source.setMinIdle(5);
			source.setMaxIdle(10);
			source.setMaxOpenPreparedStatements(100);

		} catch (Exception e) {
		}

	}

	public <T extends Object> T execute(SQLQuery query, SQLExecution<T> execution) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			connection = source.getConnection();
			statement = connection.prepareStatement(query.construct());

			for (Map.Entry<Integer, Object> entry : query.getParameters().entrySet()) {
				statement.setObject(entry.getKey(), entry.getValue());
			}

			rs = statement.executeQuery();

			return execution.execute(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
			}
		}

		return null;

	}

	public int execute(SQLQuery query) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			final String constructed = query.construct();
			connection = source.getConnection();
			statement = connection.prepareStatement(constructed);

			for (Map.Entry<Integer, Object> entry : query.getParameters().entrySet()) {
				statement.setObject(entry.getKey(), entry.getValue());
			}

			if (constructed.toLowerCase().startsWith("select")) {
				rs = statement.executeQuery();
				rs.last();
				return rs.getRow();
			} else {
				return statement.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
			}
		}

		return 0;

	}

	public int execute(String query) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			connection = source.getConnection();
			statement = connection.prepareStatement(query);

			if (query.startsWith("SELECT") || query.startsWith("select")) {
				rs = statement.executeQuery();
				rs.last();
				return rs.getRow();
			} else {
				return statement.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
			}
		}

		return 0;

	}

	/**
	 * Terminate this {@link MySQLDatabase} by closing the connection with the
	 * MySQL Server
	 */
	public void terminate() {
	}

}
