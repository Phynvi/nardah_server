package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;

public class SendSideBarInterface extends OutgoingPacket {
	
	private final int tabId;
	private final int interfaceId;
	
	public SendSideBarInterface(int tabId, int interfaceId) {
		super(71, 3);
		this.tabId = tabId;
		this.interfaceId = interfaceId;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(interfaceId).writeByte(tabId, ByteModification.ADD);
		return true;
	}
	
}
