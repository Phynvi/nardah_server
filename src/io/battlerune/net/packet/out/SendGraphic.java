package io.battlerune.net.packet.out;

import io.battlerune.game.Graphic;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.OutgoingPacket;

public class SendGraphic extends OutgoingPacket {
	
	private final Graphic graphic;
	private final Position position;
	
	public SendGraphic(Graphic graphic, Position position) {
		super(4, 6);
		this.graphic = graphic;
		this.position = position;
	}
	
	@Override
	public boolean encode(Player player) {
		player.send(new SendCoordinate(position));
		builder.writeByte(0).writeShort(graphic.getId()).writeByte(position.getHeight()).writeShort(graphic.getDelay());
		return true;
	}
}
