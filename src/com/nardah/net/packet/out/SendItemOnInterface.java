package com.nardah.net.packet.out;

import com.nardah.game.world.InterfaceConstants;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

public class SendItemOnInterface extends OutgoingPacket {
	
	private final int id;
	private final Item[] items;
	private final int[] tabAmounts;
	
	public SendItemOnInterface(int id) {
		this(id, null, new Item[]{});
	}
	
	public SendItemOnInterface(int id, Item... items) {
		this(id, null, items);
	}
	
	public SendItemOnInterface(int id, int[] tabAmounts, Item... items) {
		super(53, PacketType.VAR_SHORT);
		this.id = id;
		this.items = items;
		this.tabAmounts = tabAmounts;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(id).writeShort(items.length);
		for(final Item item : items) {
			if(item != null) {
				if(item.getAmount() > 254) {
					builder.writeByte(255).writeInt(item.getAmount(), ByteOrder.IME);
				} else {
					builder.writeByte(item.getAmount());
				}
				builder.writeShort(item.getId() + 1, ByteModification.ADD, ByteOrder.LE);
			} else {
				builder.writeByte(0).writeShort(0, ByteModification.ADD, ByteOrder.LE);
			}
		}
		
		if(id == InterfaceConstants.WITHDRAW_BANK && tabAmounts != null) {
			for(final int amount : tabAmounts) {
				builder.writeByte(amount >> 8).writeShort(amount & 0xFF);
			}
		}
		return true;
	}
}
