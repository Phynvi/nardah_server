package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;

public class SendInterfaceConfig extends OutgoingPacket {
	
	private final int main;
	private final int sub1;
	private final int sub2;
	
	public SendInterfaceConfig(int main, int sub1, int sub2) {
		super(246, 6);
		this.main = main;
		this.sub1 = sub1;
		this.sub2 = sub2;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(main, ByteOrder.LE).writeShort(sub1).writeShort(sub2);
		return true;
	}
	
}
