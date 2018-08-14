package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

/**
 * Shows an itemcontainer inside another itemcontainer.
 * @author nshusa
 */
public class SendInterfaceLayer extends OutgoingPacket {
	
	/**
	 * The id of the itemcontainer to show.
	 */
	private final int id;
	
	/**
	 * The toggle to display the itemcontainer to the user.
	 */
	private final boolean hide;
	
	/**
	 * Creates a new {@link SendInterfaceLayer).
	 * @param id The id of the itemcontainer.
	 * @param hide The toggle to display the itemcontainer.
	 */
	public SendInterfaceLayer(int id, boolean hide) {
		super(171, 3);
		this.id = id;
		this.hide = hide;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(hide ? 1 : 0).writeShort(id);
		return true;
	}
}
