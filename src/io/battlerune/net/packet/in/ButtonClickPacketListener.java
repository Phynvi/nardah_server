package io.battlerune.net.packet.in;

import io.battlerune.content.event.EventDispatcher;
import io.battlerune.content.event.impl.ClickButtonInteractionEvent;
import io.battlerune.game.event.impl.ButtonClickEvent;
import io.battlerune.game.plugin.PluginManager;
import io.battlerune.game.world.entity.mob.data.PacketType;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.net.packet.ClientPackets;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.out.SendMessage;

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

		if(player.right.isDeveloper(player)) {
		}
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
