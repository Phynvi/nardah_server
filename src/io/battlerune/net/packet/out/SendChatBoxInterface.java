package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendChatBoxInterface extends OutgoingPacket {
	
	private final int interfaceId;
	
	public SendChatBoxInterface(int interfaceId) {
		super(164, 2);
		this.interfaceId = interfaceId;
	}
	
	@Override
	public boolean encode(Player player) {
		player.interfaceManager.setDialogue(1);
		builder.writeShort(interfaceId, ByteOrder.LE);
		return true;
	}
}
