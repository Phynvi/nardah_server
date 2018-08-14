package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

/**
 * The {@code OutgoingPacket} that opens a URL from client.
 * @author Daniel | Obey
 */
public class SendURL extends OutgoingPacket {
	
	private final String link;
	
	public SendURL(String link) {
		super(138, PacketType.VAR_BYTE);
		this.link = link;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeString(link);
		return true;
	}
	
}
