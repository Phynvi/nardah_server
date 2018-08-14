package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

public class SendInterfaceAnimation extends OutgoingPacket {
	
	private final int interfaceId;
	private final int animationId;
	
	public SendInterfaceAnimation(int interfaceId, int animationId) {
		super(200, 4);
		this.interfaceId = interfaceId;
		this.animationId = animationId;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(interfaceId).writeShort(animationId);
		return true;
	}
	
}
