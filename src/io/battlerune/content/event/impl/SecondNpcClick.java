package io.battlerune.content.event.impl;

import io.battlerune.game.world.entity.actor.npc.Npc;

public class SecondNpcClick extends NpcInteractionEvent {

	public SecondNpcClick(Npc npc) {
		super(InteractionType.SECOND_CLICK_NPC, npc, 1);
	}
}
