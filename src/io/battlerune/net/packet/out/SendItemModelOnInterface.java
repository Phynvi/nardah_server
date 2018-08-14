package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendItemModelOnInterface extends OutgoingPacket {
	
	private final int id;
	private final int zoom;
	private final int model;
	
	public SendItemModelOnInterface(int id, int zoom, int model) {
		super(246, 6);
		this.id = id;
		this.zoom = zoom;
		this.model = model;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id, ByteOrder.LE).writeShort(zoom).writeShort(model);
		return true;
	}
	
}
