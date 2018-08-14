package io.battlerune.content.halloffame;

import io.battlerune.game.world.entity.mob.player.PlayerRight;

public class FameBoardPlayer {

	private PlayerRight rights;
	private String username;
	private int kills;

	public FameBoardPlayer(PlayerRight rights, String username, int kills) {
		this.rights = rights;
		this.username = username;
		this.kills = kills;
	}

	public PlayerRight getRights() {
		return rights;
	}

	public String getUsername() {
		return username;
	}

	public int getKills() {
		return kills;
	}

}
