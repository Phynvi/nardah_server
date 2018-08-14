package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendNpcDisplay extends OutgoingPacket {
	
	private final int npc;
	private final int size;
	
	public SendNpcDisplay(int npc, int size) {
		super(198, 0);
		this.npc = npc;
		this.size = size;
	}
	
	@Override
	public boolean encode(Player player) {
		return false;
		// Changes size of mob on client. gotta redo
		//		builder.writeInt(mob).writeInt(size);
		//		player.channel.ifPresent(s -> s.submit(builder));
	}
	
}
