package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendEntityHintArrow extends OutgoingPacket {
	
	private final Actor entity;
	private final boolean reset;
	
	public SendEntityHintArrow(Actor entity) {
		this(entity, false);
	}
	
	public SendEntityHintArrow(Actor entity, boolean reset) {
		super(254, 6);
		this.entity = entity;
		this.reset = reset;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(entity.isPlayer() ? reset ? -1 : 10 : reset ? -1 : 1).writeShort(entity.getIndex()).writeByte(0 >> 16).writeByte(0 >> 8).writeByte(0);
		return true;
	}
	
}
