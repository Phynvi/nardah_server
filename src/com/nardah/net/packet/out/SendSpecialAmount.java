package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

/**
 * Handles sending the special attack amount (used for the orb).
 * @author Daniel
 */
public final class SendSpecialAmount extends OutgoingPacket {
	
	public SendSpecialAmount() {
		super(137, 1);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(player.getSpecialPercentage().get());
		return true;
	}
	
}
