package io.battlerune.game.world.entity.actor.player.persist;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.login.LoginResponse;

public interface PlayerPersistable {
	
	void save(Player player);
	
	LoginResponse load(Player player, String expectedPassword);
	
}
