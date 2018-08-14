package com.nardah.content.event;

import com.nardah.game.world.entity.actor.player.Player;

public interface InteractionEventListener {
	
	boolean onEvent(Player player, InteractionEvent interactionEvent);
}