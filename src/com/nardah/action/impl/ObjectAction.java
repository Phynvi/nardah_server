package com.nardah.action.impl;

import com.nardah.action.Action;
import com.nardah.content.Obelisks;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.GameObject;
import com.nardah.net.packet.in.ObjectInteractionPacketListener;

/**
 * Action handling object action clicks.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public abstract class ObjectAction extends Action {
	
	public abstract boolean click(Player player, GameObject object, int click);
	
	public ObjectAction registerFirst(int object) {
		ObjectInteractionPacketListener.FIRST.register(object, this);
		return this;
	}
	
	public ObjectAction registerSecond(int object) {
		ObjectInteractionPacketListener.SECOND.register(object, this);
		return this;
	}
	
	public ObjectAction registerThird(int object) {
		ObjectInteractionPacketListener.THIRD.register(object, this);
		return this;
	}
	
	public static void init() {
		Obelisks.ObeliskData.init();
	}
	
}