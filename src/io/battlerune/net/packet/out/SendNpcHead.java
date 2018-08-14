package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendNpcHead extends OutgoingPacket {
	
	private final int npcId;
	private final int interfaceId;
	
	public SendNpcHead(int npcId, int interfaceId) {
		super(75, 4);
		this.npcId = npcId;
		this.interfaceId = interfaceId;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(npcId, ByteModification.ADD, ByteOrder.LE).writeShort(interfaceId, ByteModification.ADD, ByteOrder.LE);
		return true;
	}
	
}
