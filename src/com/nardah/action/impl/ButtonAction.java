package com.nardah.action.impl;

import com.nardah.action.Action;
import com.nardah.content.SkillSet;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.in.ButtonClickPacketListener;

/**
 * Action handling button clicks.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public abstract class ButtonAction extends Action {
	
	public abstract boolean click(Player player, int button);
	
	public ButtonAction register(int button) {
		ButtonClickPacketListener.BUTTONS_LISTENERS.register(button, this);
		return this;
	}
	
	public static void init() {
		SkillSet.init();
	}
	
}
