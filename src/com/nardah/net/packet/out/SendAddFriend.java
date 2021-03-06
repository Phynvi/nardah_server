package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

/**
 * Handles sending the deposit friend packet.
 * @author Daniel
 */
public final class SendAddFriend extends OutgoingPacket {
	
	private final long username;
	private int world;
	private boolean display;
	
	public SendAddFriend(long username, int world, boolean display) {
		super(50, 10);
		this.username = username;
		this.world = world;
		this.display = display;
	}
	
	public SendAddFriend(long username, int world) {
		this(username, world, true);
	}
	
	@Override
	protected boolean encode(Player player) {
		world = world != 0 ? world + 9 : world;
		builder.writeLong(username);
		builder.writeByte(world);
		builder.writeByte(display ? 1 : 0);
		return true;
	}
	
}