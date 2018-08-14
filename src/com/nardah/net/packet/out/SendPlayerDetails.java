package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;

public class SendPlayerDetails extends OutgoingPacket {
	
	public SendPlayerDetails() {
		super(249, 3);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(1, ByteModification.ADD).writeShort(player.getIndex(), ByteModification.ADD, ByteOrder.LE);
		return true;
	}
	
}
