package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

public final class SendRunEnergy extends OutgoingPacket {
	
	public SendRunEnergy() {
		super(110, 1);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(player.runEnergy);
		return true;
	}
	
}
