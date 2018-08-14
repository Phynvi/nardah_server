package com.nardah.net.packet.out;

import com.nardah.util.MessageColor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

/**
 * The {@code OutgoingPacket} that sends a message to a {@code Player}s chatbox
 * in the client.
 * @author Michael | Chex
 */
public class SendMessage extends OutgoingPacket {
	
	private final Object message;
	
	public SendMessage(Object message, MessageColor color) {
		super(253, PacketType.VAR_BYTE);
		this.message = (color == MessageColor.BLACK ? "" : "<col=" + color.getColor() + ">") + message;
	}
	
	public SendMessage(Object message) {
		this(message, MessageColor.BLACK);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeString(String.valueOf(message));
		return true;
	}
}
