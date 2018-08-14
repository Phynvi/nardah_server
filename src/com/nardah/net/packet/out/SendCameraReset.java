package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

/**
 * The {@code OutgoingPacket} resets the camera position for {@code Player}.
 * @author Daniel | Obey
 */
public class SendCameraReset extends OutgoingPacket {
	
	public SendCameraReset() {
		super(107, 0);
	}
	
	@Override
	public boolean encode(Player player) {
		return true;
	}
	
}
