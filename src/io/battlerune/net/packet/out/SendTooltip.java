package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

public class SendTooltip extends OutgoingPacket {
	
	private final String string;
	private final int id;
	
	public SendTooltip(int id, String string) {
		super(203, PacketType.VAR_SHORT);
		this.string = string;
		this.id = id;
	}
	
	public SendTooltip(String string, int id) {
		this(id, string);
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeString(string).writeShort(id, ByteModification.ADD);
		return true;
	}
	
}
