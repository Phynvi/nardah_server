package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

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
