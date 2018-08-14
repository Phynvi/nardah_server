package com.nardah.net.packet.in;

import com.nardah.content.tittle.PlayerTitle;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteOrder;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

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
