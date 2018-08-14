package io.battlerune.net.packet.in;

import io.battlerune.content.tittle.PlayerTitle;
import io.battlerune.game.world.entity.mob.UpdateFlag;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;

@PacketListenerMeta(187)
public class ColorPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		int identification = packet.readShort(ByteOrder.LE);
		int value = packet.readInt();

		if(player.right.equals(PlayerRight.OWNER) && player.debug) {
			player.send(new SendMessage("[ColorPacket] - Identification: " + identification + " Value: " + value, MessageColor.DEVELOPER));
		}

		switch(identification) {

			case 0:
				player.playerTitle = PlayerTitle.create(player.playerTitle.getTitle(), value);
				player.updateFlags.add(UpdateFlag.APPEARANCE);
				break;

			case 1:
				// yell
				break;
		}
	}
}
