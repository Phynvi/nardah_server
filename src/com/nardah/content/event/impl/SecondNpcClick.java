package com.nardah.content.event.impl;

import com.nardah.game.world.entity.actor.mob.Mob;

public class SecondNpcClick extends NpcInteractionEvent {

	public SecondNpcClick(Mob mob) {
		super(InteractionType.SECOND_CLICK_NPC, mob, 1);
	}
}
