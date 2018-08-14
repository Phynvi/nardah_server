package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendPlayerDialogueHead extends OutgoingPacket {
	
	private final int interfaceId;
	
	public SendPlayerDialogueHead(int interfaceId) {
		super(185, 2);
		this.interfaceId = interfaceId;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(interfaceId, ByteModification.ADD, ByteOrder.LE);
		return true;
	}
	
}
