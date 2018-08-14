package com.nardah.net.packet.out;

import com.nardah.game.world.position.Position;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;

public class SendCoordinate extends OutgoingPacket {
	
	private final Position position;
	
	public SendCoordinate(Position position) {
		super(85, 2);
		this.position = position;
	}
	
	@Override
	public boolean encode(Player player) {
		final int y = position.getLocalY(player.lastPosition);
		final int x = position.getLocalX(player.lastPosition);
		builder.writeByte(y, ByteModification.NEG);
		builder.writeByte(x, ByteModification.NEG);
		return true;
	}
	
}
