package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.ground.GroundItem;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.OutgoingPacket;

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
