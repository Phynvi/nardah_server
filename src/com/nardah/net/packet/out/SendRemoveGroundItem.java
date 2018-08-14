package com.nardah.net.packet.out;

import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;

public class SendRemoveGroundItem extends OutgoingPacket {
	
	private final GroundItem groundItem;
	
	public SendRemoveGroundItem(GroundItem groundItem) {
		super(156, 3);
		this.groundItem = groundItem;
	}
	
	@Override
	public boolean encode(Player player) {
		player.send(new SendCoordinate(groundItem.getPosition()));
		builder.writeByte(0, ByteModification.ADD).writeShort(groundItem.item.getId());
		return true;
	}
	
}
