package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;

public class SendPoison extends OutgoingPacket {
	
	public enum PoisonType {
		NO_POISON, REGULAR, VENOM
	}
	
	private final PoisonType type;
	
	public SendPoison(PoisonType type) {
		super(182, 1);
		this.type = type;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(type.ordinal(), ByteModification.NEG);
		return true;
	}
	
}
