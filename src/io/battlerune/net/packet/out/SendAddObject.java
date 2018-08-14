package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendAddObject extends OutgoingPacket {
	
	private final GameObject object;
	
	public SendAddObject(GameObject object) {
		super(151, 4);
		this.object = object;
	}
	
	@Override
	public boolean encode(Player player) {
		if(object.getInstancedHeight() != player.instance) {
			return false;
		}
		player.send(new SendCoordinate(object.getPosition()));
		builder.writeByte(0, ByteModification.ADD);
		builder.writeShort(object.getId(), ByteOrder.LE);
		builder.writeByte((object.getObjectType().getId() << 2) + (object.getDirection().getId() & 3), ByteModification.SUB);
		return true;
	}
	
}