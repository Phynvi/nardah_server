package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;

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
