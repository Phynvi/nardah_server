package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendMiniMeData extends OutgoingPacket {
	
	private final int model;
	
	public SendMiniMeData(int model) {
		super(157, 2);
		this.model = model;
	}
	
	@Override
	protected boolean encode(Player player) {
		builder.writeShort(model);
		return true;
	}
	
}
