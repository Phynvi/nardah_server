package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;

public class SendWalkableInterface extends OutgoingPacket {
	
	private final int id;
	
	public SendWalkableInterface(int id) {
		super(208, 2);
		this.id = id;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id, ByteOrder.LE);
		return true;
	}
	
}
