package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public final class SendMysteryBoxResult extends OutgoingPacket {
	
	private final int result;
	
	public SendMysteryBoxResult(int result) {
		super(55, 1);
		this.result = result;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(result);
		return true;
	}
}
