package com.nardah.net.packet.out;

import com.nardah.game.world.object.GameObject;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;

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