package com.nardah.content.event;

import com.nardah.content.activity.Activity;
import com.nardah.content.event.InteractionEvent.InteractionType;
import com.nardah.game.world.entity.actor.player.Player;

public class EventDispatcher {
	
	private final InteractionEvent interactionEvent;
	
	public EventDispatcher(InteractionEvent interactionEvent) {
		this.interactionEvent = interactionEvent;
	}
	
	public static boolean execute(Player player, InteractionEvent event) {
		return player.inActivity() && Activity.evaluate(player, it -> it.onEvent(player, event)) || player.quest.onEvent(event) || player.skills.onEvent(event);
	}
	
	public void dispatch(InteractionType type, EventHandler eventHandler) {
		if(interactionEvent.isHandled()) {
			return;
		}
		
		if(interactionEvent.getType() == type) {
			interactionEvent.setHandled(eventHandler.handle(interactionEvent));
		}
	}
	
}
