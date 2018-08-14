package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

public class SendAddIgnore extends OutgoingPacket {
	
	private final long usernameLong;
	
	public SendAddIgnore(long usernameLong) {
		super(214, 8);
		this.usernameLong = usernameLong;
	}
	
	@Override
	protected boolean encode(Player player) {
		builder.writeLong(usernameLong);
		return true;
	}
	
}
