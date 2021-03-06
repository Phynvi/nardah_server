package com.nardah.net.packet;

import com.nardah.game.world.entity.actor.player.Player;

/**
 * The itemcontainer that allows any implementing {@Packet}s. The ability to be
 * intercepted as an in {@code Packet}.
 * @author SeVen
 */
@PacketListenerMeta
public interface PacketListener {

	/**
	 * Handles the packet that has just been received.
	 * @param player The player receiving this packet.
	 * @param packet The packet that has been received.
	 */
	void handlePacket(Player player, GamePacket packet);
}
