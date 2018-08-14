package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

/**
 * Handles sending the progress bar data to the client.
 * @author Daniel
 */
public class SendProgressBar extends OutgoingPacket {
	
	private final int id;
	private final int amount;
	private final String message;
	
	public SendProgressBar(int id, int amount) {
		this(id, amount, "");
	}
	
	public SendProgressBar(int id, int amount, String message) {
		super(129, PacketType.VAR_BYTE);
		this.id = id;
		this.amount = amount;
		this.message = message;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id).writeShort(amount).writeString(String.valueOf(message));
		return true;
	}
	
}
