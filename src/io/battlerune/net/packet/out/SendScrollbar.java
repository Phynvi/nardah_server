package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

public class SendScrollbar extends OutgoingPacket {
	
	private final int scrollbar;
	private final int size;
	
	public SendScrollbar(int scrollbar, int size) {
		super(204, 8);
		this.scrollbar = scrollbar;
		this.size = size;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeInt(scrollbar).writeInt(size);
		return true;
	}
	
}
