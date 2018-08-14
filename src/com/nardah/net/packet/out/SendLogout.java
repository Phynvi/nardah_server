package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendLogout extends OutgoingPacket {
	
	public SendLogout() {
		super(109, 0);
	}
	
	@Override
	public boolean encode(Player player) {
		return true;
	}
	
}
