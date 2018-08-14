package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;

public class SendMoveComponent extends OutgoingPacket {
	
	private final int id;
	private final int x, y;
	
	public SendMoveComponent(int x, int y, int id) {
		super(70, 6);
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(x).writeShort(y, ByteOrder.LE).writeShort(id, ByteOrder.LE);
		return true;
	}
	
}
