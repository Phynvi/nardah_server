package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendPlayerDetails extends OutgoingPacket {
	
	public SendPlayerDetails() {
		super(249, 3);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(1, ByteModification.ADD).writeShort(player.getIndex(), ByteModification.ADD, ByteOrder.LE);
		return true;
	}
	
}
