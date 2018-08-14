package io.battlerune.game.world.entity.actor.player.persist;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.login.LoginResponse;

public final class PlayerSerializer {
	
	private static final PlayerPersistable perstable = /* Config.FORUM_INTEGRATION ? new PlayerPersistDB() : */new PlayerPersistFile();
	
	public static void save(Player player) {
		if(player.isBot) {
			return;
		}
		// player save thread
		new Thread(() -> perstable.save(player)).start();
	}
	
	public static LoginResponse load(Player player, String expectedPassword) {
		if(player.isBot) {
			return LoginResponse.COULD_NOT_COMPLETE_LOGIN;
		}
		
		return perstable.load(player, expectedPassword);
	}
}
