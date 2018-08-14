package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

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
