package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.OutgoingPacket;

public class SendForceTab extends OutgoingPacket {
	
	private final int id;
	
	public SendForceTab(int id) {
		super(106, 1);
		this.id = id;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(id, ByteModification.NEG);
		return true;
	}
	
}
