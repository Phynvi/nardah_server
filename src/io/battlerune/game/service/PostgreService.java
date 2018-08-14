package io.battlerune.game.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.battlerune.Config;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class PostgreService {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.DATABASE);
	
	private static HikariDataSource connectionPool;
	
	public static void start() throws Exception {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("org.postgresql.Driver");
		config.setJdbcUrl(Config.POSTGRE_URL);
		config.setUsername(Config.POSTGRE_USER);
		config.setPassword(Config.POSTGRE_PASS);
		config.setMaximumPoolSize(50);
		config.setConnectionTimeout(10_000);
		config.setIdleTimeout(0);
		config.setMaxLifetime(0);
		config.addDataSourceProperty("cachePrepStmts", "true");
		connectionPool = new HikariDataSource(config);
		logger.info("Successfully connected to game database.");
	}
	
	public static Connection getConnection() throws SQLException {
		if(!Config.LIVE_SERVER) {
			return DriverManager.getConnection(Config.POSTGRE_URL, Config.POSTGRE_USER, Config.POSTGRE_PASS);
		}
		return connectionPool.getConnection();
	}
	
}
