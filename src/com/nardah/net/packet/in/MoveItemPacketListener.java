package com.nardah.net.packet.in;

import com.nardah.game.world.InterfaceConstants;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

@PacketListenerMeta(ClientPackets.MOVE_ITEM)
public class MoveItemPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		if(player.locking.locked(PacketType.MOVE_ITEMS))
			return;

		final int interfaceId = packet.readShort(ByteOrder.LE, ByteModification.ADD);
		final int inserting = packet.readByte(ByteModification.NEG);
		final int fromSlot = packet.readShort(ByteOrder.LE, ByteModification.ADD);
		final int toSlot = packet.readShort(ByteOrder.LE);

		if(player.idle) {
			player.idle = false;
		}

		switch(interfaceId) {
			case InterfaceConstants.INVENTORY_INTERFACE:
			case InterfaceConstants.INVENTORY_STORE:
				player.inventory.swap(fromSlot, toSlot);
				break;

			case InterfaceConstants.WITHDRAW_BANK:
				player.bank.moveItem(inserting, fromSlot, toSlot);
				break;

			default:
				System.out.println("Unkown Item movement itemcontainer id: " + interfaceId);
				break;
		}
	}
}
