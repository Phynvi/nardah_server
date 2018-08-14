package com.nardah.net.packet.in;

import com.nardah.content.event.EventDispatcher;
import com.nardah.content.event.impl.ClickButtonInteractionEvent;
import com.nardah.game.event.impl.ButtonClickEvent;
import com.nardah.game.plugin.PluginManager;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@code GamePacket} responsible for clicking buttons on the client.
 * @author Daniel | Obey
 */
@PacketListenerMeta(ClientPackets.BUTTON_CLICK)
public class ButtonClickPacketListener implements PacketListener {

	@SuppressWarnings("static-access")
	@Override
	public void handlePacket(final Player player, GamePacket packet) {
		final int button = packet.readShort();

		if(player.isDead()) {
			return;
		}

		if(player.locking.locked(PacketType.CLICK_BUTTON, button)) {
			return;
		}

		if(PlayerRight.isDeveloper(player)) {
			player.send(new SendMessage(String.format("[%s]: button=%d", ButtonClickPacketListener.class.getSimpleName(), button)));
		}

		if(EventDispatcher.execute(player, new ClickButtonInteractionEvent(button))) {
			return;
		}

		PluginManager.getDataBus().publish(player, new ButtonClickEvent(button));
	}
}
