package com.nardah.content.event.impl;

import com.nardah.game.world.entity.actor.mob.Mob;

public class FirstNpcClick extends NpcInteractionEvent {

	public FirstNpcClick(Mob mob) {
		super(InteractionType.FIRST_CLICK_NPC, mob, 0);
	}
}
