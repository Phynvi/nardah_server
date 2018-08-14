package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendWalkableInterface extends OutgoingPacket {
	
	private final int id;
	
	public SendWalkableInterface(int id) {
		super(208, 2);
		this.id = id;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id, ByteOrder.LE);
		return true;
	}
	
}
