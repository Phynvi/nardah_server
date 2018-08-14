package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public final class SendRunEnergy extends OutgoingPacket {
	
	public SendRunEnergy() {
		super(110, 1);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(player.runEnergy);
		return true;
	}
	
}
