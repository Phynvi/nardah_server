package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public final class SendZoom extends OutgoingPacket {
	
	private final int zoom;
	
	public SendZoom(int zoom) {
		super(139, 2);
		this.zoom = zoom;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(zoom);
		return true;
	}
}
