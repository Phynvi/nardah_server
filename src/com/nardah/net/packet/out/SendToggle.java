package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;

public final class SendToggle extends OutgoingPacket {
	
	private final int id;
	private final int value;
	
	public SendToggle(int id, int value) {
		super(87, 6);
		this.id = id;
		this.value = value;
	}
	
	@Override
	protected boolean encode(Player player) {
		builder.writeShort(id, ByteOrder.LE).writeInt(value, ByteOrder.ME);
		return true;
	}
	
}
