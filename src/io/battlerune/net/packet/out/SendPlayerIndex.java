package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

/**
 * Sends a player index to the client.
 * Created by Daniel on 2016-12-04.
 */
public class SendPlayerIndex extends OutgoingPacket {
	
	private final int index;
	
	public SendPlayerIndex(int index) {
		super(201, 2);
		this.index = index;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(index);
		return true;
	}
	
}
