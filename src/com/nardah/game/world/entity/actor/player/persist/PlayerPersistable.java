package com.nardah.game.world.entity.actor.player.persist;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.login.LoginResponse;

public interface PlayerPersistable {
	
	void save(Player player);
	
	LoginResponse load(Player player, String expectedPassword);
	
}
