package com.nardah.game.world.entity.actor.player.persist;

import com.google.gson.JsonElement;
import com.nardah.game.world.entity.actor.player.Player;

abstract class PlayerJSONProperty {
	
	final String label;
	
	PlayerJSONProperty(String label) {
		this.label = label;
	}
	
	abstract void read(Player player, JsonElement property);
	
	abstract Object write(Player player);
	
}
