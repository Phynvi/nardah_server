package com.nardah.game.world.entity.actor.data;

import com.nardah.game.Graphic;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendWidget;

/**
 * Holds all the lock types.
 * @author Daniel
 */
public enum LockType {
	MASTER(PacketType.values()) {
		@Override
		public void execute(Actor actor, int time) {
			if(actor.isPlayer())
				actor.getPlayer().action.reset();
		}
	}, MASTER_WITH_MOVEMENT(PacketType.MASTER_WITH_MOVEMENT) {
		@Override
		public void execute(Actor actor, int time) {
			if(actor.isPlayer())
				actor.getPlayer().action.reset();
		}
	}, MASTER_WITH_COMMANDS(PacketType.MASTER_WITH_COMMANDS) {
		@Override
		public void execute(Actor actor, int time) {
			if(actor.isPlayer())
				actor.getPlayer().action.reset();
		}
	}, OBJECT(PacketType.CLICK_OBJECT) {
		@Override
		public void execute(Actor actor, int time) {
			actor.movement.reset();
		}
	}, WALKING(PacketType.WALKING, PacketType.MOVEMENT) {
		@Override
		public void execute(Actor actor, int time) {
		}
	}, STUN(PacketType.WALKING, PacketType.COMBAT, PacketType.PICKUP_ITEM, PacketType.WIELD_ITEM, PacketType.COMMANDS, PacketType.CLICK_BUTTON, PacketType.CLICK_NPC, PacketType.CLICK_OBJECT, PacketType.USE_ITEM, PacketType.INTERACT) {
		@Override
		public void execute(Actor actor, int time) {
			if(actor.locking.locked() && actor.locking.getLock() == STUN)
				return;
			if(actor.isPlayer()) {
				Player player = actor.getPlayer();
				player.resetFace();
				player.graphic(new Graphic(80, true));
				player.send(new SendMessage("You have been stunned!"));
				player.send(new SendWidget(SendWidget.WidgetType.STUN, time));
			}
		}
	}, FREEZE(PacketType.WALKING, PacketType.MOVEMENT) {
		@Override
		public void execute(Actor actor, int time) {
			if(actor.locking.locked() && actor.locking.getLock() == FREEZE)
				return;
			if(actor.isPlayer()) {
				Player player = actor.getPlayer();
				player.resetFace();
				player.send(new SendMessage("You've been frozen!"));
				player.send(new SendWidget(SendWidget.WidgetType.FROZEN, time));
			}
		}
	};
	
	/**
	 * The lock packet flag.
	 */
	public final PacketType[] packets;
	
	/**
	 * Handles the execution of the lock.
	 */
	public abstract void execute(Actor actor, int time);
	
	/**
	 * Constructs a new <code>LockType</code>.
	 */
	LockType(PacketType... packets) {
		this.packets = packets;
	}
	
	public boolean isLocked(PacketType type) {
		return isLocked(type, null, null);
	}
	
	public boolean isLocked(PacketType type, Actor actor, Object object) {
		for(PacketType packet : packets) {
			if(type == packet)
				return !packet.exception(actor, object);
		}
		return false;
	}
}
