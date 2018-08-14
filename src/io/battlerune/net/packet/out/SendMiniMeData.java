package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

public class SendMiniMeData extends OutgoingPacket {
	
	private final int model;
	
	public SendMiniMeData(int model) {
		super(157, 2);
		this.model = model;
	}
	
	@Override
	protected boolean encode(Player player) {
		builder.writeShort(model);
		return true;
	}
	
}
