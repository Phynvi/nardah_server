package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

public class SendKillFeed extends OutgoingPacket {
	
	private final String killer;
	private final String victim;
	
	public SendKillFeed(String killer, String victim) {
		super(173, PacketType.VAR_BYTE);
		this.killer = killer;
		this.victim = victim;
	}
	
	@Override
	public boolean encode(Player player) {
		if(killer == null || killer.length() == 0 || victim == null || victim.length() == 0) {
			return false;
		}
		builder.writeString(killer).writeString(victim);
		return true;
	}
	
}
