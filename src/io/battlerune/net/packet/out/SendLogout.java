package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

public class SendLogout extends OutgoingPacket {
	
	public SendLogout() {
		super(109, 0);
	}
	
	@Override
	public boolean encode(Player player) {
		return true;
	}
	
}
