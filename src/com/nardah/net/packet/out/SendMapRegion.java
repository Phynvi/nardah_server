package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;

public class SendMapRegion extends OutgoingPacket {
	
	public SendMapRegion() {
		super(73, 4);
	}
	
	@Override
	public boolean encode(Player player) {
		player.lastPosition = player.getPosition().copy();
		builder.writeShort(player.getPosition().getChunkX() + 6, ByteModification.ADD).writeShort(player.getPosition().getChunkY() + 6);
		return true;
	}
	
}
