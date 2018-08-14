package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

public class SendTooltip extends OutgoingPacket {
	
	private final String string;
	private final int id;
	
	public SendTooltip(int id, String string) {
		super(203, PacketType.VAR_SHORT);
		this.string = string;
		this.id = id;
	}
	
	public SendTooltip(String string, int id) {
		this(id, string);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeString(string).writeShort(id, ByteModification.ADD);
		return true;
	}
	
}
