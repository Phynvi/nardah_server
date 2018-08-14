package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.OutgoingPacket;

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
