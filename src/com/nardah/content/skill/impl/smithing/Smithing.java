package com.nardah.content.skill.impl.smithing;

import com.nardah.content.event.impl.ClickButtonInteractionEvent;
import com.nardah.content.event.impl.ItemContainerInteractionEvent;
import com.nardah.content.event.impl.ItemOnObjectInteractionEvent;
import com.nardah.content.event.impl.ObjectInteractionEvent;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

/**
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 11-2-2017.
 */
public final class Smithing extends Skill {
	public Smithing(int level, double experience) {
		super(Skill.SMITHING, level, experience);
	}

	@Override
	public boolean itemContainerAction(Player player, ItemContainerInteractionEvent event) {
		switch(event.id) {
			case 1:
				return SmithingArmour.forge(player, event.interfaceId, event.removeSlot, 1);
			case 2:
				return SmithingArmour.forge(player, event.interfaceId, event.removeSlot, 5);
			case 3:
				return SmithingArmour.forge(player, event.interfaceId, event.removeSlot, 10);
			default:
				return false;
		}
	}

	@Override
	public boolean clickButton(Player player, ClickButtonInteractionEvent event) {
		switch(event.getType()) {
			case CLICK_BUTTON:
				return Smelting.smelt(player, event.getButton());
			default:
				return false;
		}
	}

	@Override
	public boolean useItem(Player player, ItemOnObjectInteractionEvent event) {
		switch(event.getType()) {
			case ITEM_ON_OBJECT:
				return SmithingArmour.openInterface(player, event.getItem(), event.getObject());
			default:
				return false;
		}
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		switch(event.getType()) {
			case FIRST_CLICK_OBJECT:
			case SECOND_CLICK_OBJECT:
				return Smelting.openInterface(player, event.getObject());
			default:
				return false;
		}
	}
}
