package com.nardah.action.impl;

import com.nardah.action.Action;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.in.MobInteractionPacketListener;

/**
 * Action handling npc action clicks.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public abstract class MobAction extends Action {
	
	public abstract boolean click(Player player, Mob mob, int click);
	
	public MobAction registerFirst(int npc) {
		MobInteractionPacketListener.FIRST.register(npc, this);
		return this;
	}
	
	public MobAction registerSecond(int npc) {
		MobInteractionPacketListener.SECOND.register(npc, this);
		return this;
	}
	
	public MobAction registerThird(int npc) {
		MobInteractionPacketListener.THIRD.register(npc, this);
		return this;
	}
	
	public MobAction registerFourth(int npc) {
		MobInteractionPacketListener.FOURTH.register(npc, this);
		return this;
	}
	
	public static void init() {
	
	}
	
}
