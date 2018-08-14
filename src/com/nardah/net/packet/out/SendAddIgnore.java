package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendAddIgnore extends OutgoingPacket {
	
	private final long usernameLong;
	
	public SendAddIgnore(long usernameLong) {
		super(214, 8);
		this.usernameLong = usernameLong;
	}
	
	@Override
	protected boolean encode(Player player) {
		builder.writeLong(usernameLong);
		return true;
	}
	
}
