package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;

/**
 * The {@code OutgoingPacket} that opens an itemcontainer for {@code Player}.
 * @author Daniel | Obey
 */
public class SendInterface extends OutgoingPacket {
	
	private final int interfaceId;
	
	public SendInterface(int interfaceId) {
		super(97, 2);
		this.interfaceId = interfaceId;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeShort(interfaceId);
		return true;
	}
	
}
