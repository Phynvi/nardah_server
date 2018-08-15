package com.nardah.game.world.entity.actor.data;

import com.nardah.content.activity.ActivityType;
import com.nardah.content.dialogue.Dialogue;
import com.nardah.content.event.EventDispatcher;
import com.nardah.content.event.impl.ClickButtonInteractionEvent;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.action.but.StarterKitButtonAction;

public enum PacketType {
	COMBAT {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, KEY {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, CLICK_ITEM {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, CLICK_OBJECT {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, COMMANDS {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, CLICK_NPC {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, CLICK_BUTTON {
		@Override
		public boolean exception(Actor actor, Object object) {
			if(!actor.isPlayer())
				return false;
			
			int button = (int) object;
			
			if(button == 2458)// logout
				return true;
			if(Dialogue.isDialogueButton(button))
				return true;
			if(StarterKitButtonAction.isButton(button))
				return true;
			if(actor.inActivity(ActivityType.TUTORIAL) && EventDispatcher.execute(actor.getPlayer(), new ClickButtonInteractionEvent(button)))
				return true;
			
			return false;
		}
	}, USE_ITEM {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, WIELD_ITEM {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, MOVE_ITEMS {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, PICKUP_ITEM {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, USE_MAGIC {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, DROP_ITEM {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, INTERACT {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, MOVEMENT {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, WALKING {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	}, CHAT {
		@Override
		public boolean exception(Actor actor, Object object) {
			return false;
		}
	};
	
	/**
	 * Handles the execution of the lock.
	 */
	public abstract boolean exception(Actor actor, Object object);
	
	public static final PacketType[] MASTER_WITH_MOVEMENT = {CLICK_ITEM, CLICK_OBJECT, COMMANDS, CLICK_NPC, CLICK_BUTTON, USE_ITEM, WIELD_ITEM, MOVE_ITEMS, PICKUP_ITEM, USE_MAGIC, DROP_ITEM, INTERACT, WALKING, CHAT, KEY, COMBAT};
	
	public static final PacketType[] MASTER_WITH_COMMANDS = {CLICK_ITEM, CLICK_OBJECT, CLICK_NPC, CLICK_BUTTON, USE_ITEM, WIELD_ITEM, MOVE_ITEMS, PICKUP_ITEM, USE_MAGIC, DROP_ITEM, INTERACT, WALKING, CHAT, KEY, COMBAT, WALKING, MOVEMENT};
}
