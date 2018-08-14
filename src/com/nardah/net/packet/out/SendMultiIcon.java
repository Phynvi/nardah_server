package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendMultiIcon extends OutgoingPacket {
	
	private final int icon;
	
	public SendMultiIcon(int icon) {
		super(61, 1);
		this.icon = icon;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(icon);
		return true;
	}
	
}
