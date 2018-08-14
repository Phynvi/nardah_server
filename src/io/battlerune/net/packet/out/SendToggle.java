package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public final class SendToggle extends OutgoingPacket {
	
	private final int id;
	private final int value;
	
	public SendToggle(int id, int value) {
		super(87, 6);
		this.id = id;
		this.value = value;
	}
	
	@Override
	protected boolean encode(Player player) {
		builder.writeShort(id, ByteOrder.LE).writeInt(value, ByteOrder.ME);
		return true;
	}
	
}
