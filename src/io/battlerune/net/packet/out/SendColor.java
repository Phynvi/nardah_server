package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

/**
 * The {@code OutgoingPacket} that sends a color to a string in the client.
 * @author Daniel | Obey
 */
public class SendColor extends OutgoingPacket {
	
	private final int id;
	private final int color;
	
	public SendColor(int id, int color) {
		super(122, 6);
		this.id = id;
		this.color = color;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id, ByteModification.ADD, ByteOrder.LE).writeInt(color);
		return true;
	}
	
}
