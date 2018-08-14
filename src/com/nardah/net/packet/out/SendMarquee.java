package com.nardah.net.packet.out;

import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

public class SendMarquee extends OutgoingPacket {
	
	private final String[] strings;
	private final int id;
	
	public SendMarquee(int id, String... strings) {
		super(205, PacketType.VAR_SHORT);
		this.strings = strings;
		this.id = id;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id, ByteModification.ADD);
		for(int index = 0; index < 5; index++) {
			builder.writeString(index >= strings.length ? "" : strings[index].replace("#players", World.getPlayerCount() + ""));
		}
		return true;
	}
}
