package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

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
