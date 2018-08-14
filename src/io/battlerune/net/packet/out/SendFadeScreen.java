package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

/**
 * Handles sending the fade screen packet.
 * @author Daniel
 */
public class SendFadeScreen extends OutgoingPacket {
	
	private final String message;
	private final int state;
	private final int seconds;
	
	public SendFadeScreen(String message, int state, int seconds) {
		super(189, PacketType.VAR_SHORT);
		this.message = message;
		this.state = state;
		this.seconds = seconds;
	}
	
	@Override
	public boolean encode(Player player) {
		player.interfaceManager.close();
		builder.writeString(message).writeByte(state).writeByte(seconds);
		return true;
	}
	
}
