package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;
import io.battlerune.util.MessageColor;

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
