package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

/**
 * The {@code OutgoingPacket} responsible for sending game messages.
 * @author Daniel
 */
public class SendGameMessage extends OutgoingPacket {
	
	private final int id;
	private final int time;
	private final Object context;
	
	public SendGameMessage(int id, int time, Object context) {
		super(114, PacketType.VAR_BYTE);
		this.id = id;
		this.time = time;
		this.context = context;
	}
	
	public SendGameMessage(int id, Object context) {
		super(114, PacketType.VAR_BYTE);
		this.id = id;
		this.time = -1;
		this.context = context;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id).writeShort(time * 3000).writeString(String.valueOf(context));
		return true;
	}
	
}
