package io.battlerune.net.packet.in;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.net.packet.ClientPackets;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;

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