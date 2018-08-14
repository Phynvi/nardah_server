package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

public class SendPrivateMessage extends OutgoingPacket {
	
	private final long name;
	private final PlayerRight rights;
	private final byte[] compressed;
	
	public SendPrivateMessage(long name, PlayerRight rights, byte[] compressed) {
		super(196, PacketType.VAR_BYTE);
		this.name = name;
		this.rights = rights;
		this.compressed = compressed;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeLong(name).writeInt(player.relations.getPrivateMessageId()).writeByte(rights.getCrown()).writeBytes(compressed, compressed.length);
		return true;
	}
	
}
