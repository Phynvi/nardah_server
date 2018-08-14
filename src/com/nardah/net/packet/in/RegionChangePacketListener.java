package com.nardah.net.packet.in;

import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@link GamePacket}'s responsible for changing a players region. Used when
 * a player enters a new map region or when the map region has been successfully
 * loaded.
 * @author Daniel
 */
@PacketListenerMeta({ClientPackets.LOADED_REGION, ClientPackets.ENTER_REGION})
public class RegionChangePacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		switch(packet.getOpcode()) {
			case ClientPackets.ENTER_REGION:
				int a = packet.readInt();
				if(player.debug && PlayerRight.isDeveloper(player)) {
					player.send(new SendMessage("[REGION] Entered new region: " + a, MessageColor.DEVELOPER));
				}
				if(a != 0x3f008edd) {
					player.logout();
				}
				break;

			case ClientPackets.LOADED_REGION:
				player.loadRegion();
				break;
		}
	}
}