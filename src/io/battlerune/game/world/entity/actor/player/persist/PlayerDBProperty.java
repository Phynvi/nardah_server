package io.battlerune.game.world.entity.actor.player.persist;

import com.jcabi.jdbc.JdbcSession;
import io.battlerune.game.world.entity.actor.player.Player;

import java.sql.SQLException;

public abstract class PlayerDBProperty {
	
	private final String name;
	
	public PlayerDBProperty(String name) {
		this.name = name;
	}
	
	abstract void read(Player player, JdbcSession session) throws SQLException;
	
	abstract void write(Player player, JdbcSession session) throws SQLException;
	
	public String getName() {
		return name;
	}
	
}
