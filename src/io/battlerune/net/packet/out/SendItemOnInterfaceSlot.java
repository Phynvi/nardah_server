package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

public class SendItemOnInterfaceSlot extends OutgoingPacket {
	private final int interfaceId;
	private final Item item;
	private final int slot;
	
	public SendItemOnInterfaceSlot(int interfaceId, int item, int slot) {
		this(interfaceId, new Item(item, 1), slot);
	}
	
	public SendItemOnInterfaceSlot(int interfaceId, int item, int amount, int slot) {
		this(interfaceId, new Item(item, amount), slot);
	}
	
	public SendItemOnInterfaceSlot(int interfaceId, Item item, int slot) {
		super(34, PacketType.VAR_SHORT);
		this.interfaceId = interfaceId;
		this.item = item;
		this.slot = slot;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(interfaceId);
		builder.writeByte(slot);
		if(item == null) {
			builder.writeShort(0);
			builder.writeByte(0);
		} else {
			builder.writeShort(item.getId() + 1);
			if(item.getAmount() > 254) {
				builder.writeByte(255);
				builder.writeInt(item.getAmount());
			} else {
				builder.writeByte(item.getAmount());
			}
		}
		return true;
	}
	
}
