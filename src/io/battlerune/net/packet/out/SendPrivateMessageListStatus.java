package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.relations.PrivateMessageListStatus;
import io.battlerune.net.packet.OutgoingPacket;

public class SendPrivateMessageListStatus extends OutgoingPacket {
	
	private final PrivateMessageListStatus status;
	
	public SendPrivateMessageListStatus(PrivateMessageListStatus status) {
		super(221, 1);
		this.status = status;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(status.ordinal());
		return true;
	}
	
}