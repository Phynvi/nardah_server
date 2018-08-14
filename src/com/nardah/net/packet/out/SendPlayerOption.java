package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.PlayerOption;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

/**
 * Shows a player options such as right clicking a player.
 * @author Daniel
 */
public final class SendPlayerOption extends OutgoingPacket {
	
	private PlayerOption option;
	private boolean top;
	private boolean disable;
	
	public SendPlayerOption(PlayerOption option, boolean top) {
		this(option, top, false);
	}
	
	public SendPlayerOption(PlayerOption option, boolean top, boolean disable) {
		super(104, PacketType.VAR_BYTE);
		this.option = option;
		this.top = top;
		this.disable = disable;
	}
	
	@Override
	public boolean encode(Player player) {
		if(player.contextMenus.contains(option) && !disable) {
			return false;
		}
		
		builder.writeByte(option.getIndex(), ByteModification.NEG).writeByte(top ? 1 : 0, ByteModification.ADD).writeString(disable ? "null" : option.getName());
		
		if(disable) {
			player.contextMenus.remove(option);
		} else {
			player.contextMenus.add(option);
		}
		return true;
	}
	
}
