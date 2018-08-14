package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;

public class SendForceTab extends OutgoingPacket {
	
	private final int id;
	
	public SendForceTab(int id) {
		super(106, 1);
		this.id = id;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(id, ByteModification.NEG);
		return true;
	}
	
}
