package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

/**
 * The {@code OutgoingPacket} that sends a font to a string in the client.
 * @author Daniel | Obey
 */
public class SendFont extends OutgoingPacket {
	
	private final int id;
	private final int font;
	
	public SendFont(int id, int font) {
		super(123, 6);
		this.id = id;
		this.font = font;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id, ByteModification.ADD, ByteOrder.LE).writeInt(font);
		return true;
	}
	
}
