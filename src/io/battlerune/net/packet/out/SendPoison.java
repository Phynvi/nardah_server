package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.OutgoingPacket;

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
