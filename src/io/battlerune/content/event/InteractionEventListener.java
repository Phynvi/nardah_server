package io.battlerune.content.event;

import io.battlerune.game.world.entity.mob.player.Player;

public interface InteractionEventListener {
	
	boolean onEvent(Player player, InteractionEvent interactionEvent);
}