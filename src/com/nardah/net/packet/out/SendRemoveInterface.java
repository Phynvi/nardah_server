package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendRemoveInterface extends OutgoingPacket {
	
	private final boolean close;
	
	public SendRemoveInterface(boolean close) {
		super(219, 1);
		this.close = close;
	}
	
	public SendRemoveInterface() {
		this(true);
	}
	
	@Override
	public boolean encode(Player player) {
		byte val = (byte) (close ? 1 : 0);
		builder.writeByte(val);
		return true;
	}
	
}
