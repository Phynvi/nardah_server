package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.OutgoingPacket;

public class SendObjectAnimation extends OutgoingPacket {
	
	private final int animation;
	private final GameObject object;
	
	public SendObjectAnimation(int animation, GameObject object) {
		super(160, 4);
		this.animation = animation;
		this.object = object;
	}
	
	@Override
	public boolean encode(Player player) {
		player.send(new SendCoordinate(object.getPosition()));
		builder.writeByte(0, ByteModification.SUB).writeByte((object.getObjectType().getId() << 2) + (object.getDirection().getId() & 3), ByteModification.SUB).writeShort(animation, ByteModification.ADD);
		return true;
	}
	
}
