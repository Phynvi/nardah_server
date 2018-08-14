package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

/**
 * Makes a tab blink/flash.
 * @author Daniel
 */
public class SendTabBlink extends OutgoingPacket {
	
	private int tab;
	
	public SendTabBlink(int tab) {
		super(24, 1);
		this.tab = tab;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(tab);
		return true;
	}
	
}
