package com.nardah.net.packet.in;

import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@link GamePacket} responsible logging out a player after a certain
 * amount of time.
 * @author Daniel
 */
@PacketListenerMeta(202)
public class IdleLogoutPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		if(player.idle)
			return;

		player.idle = true;
		player.send(new SendMessage("There has not been any detection from your account. You have now been listed as idle.", MessageColor.DEVELOPER));
		player.send(new SendMessage("You will not receive royalty points until you are active.", MessageColor.DEVELOPER));
	}
}