package com.nardah.net.packet.in;

import com.nardah.net.packet.out.SendMessage;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

import java.awt.event.KeyEvent;

/**
 * The {@code GamePacket} responsible for clicking keyboard buttons.
 * @author Daniel | Obey
 */
@PacketListenerMeta(186)
public class KeyPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		final int key = packet.readShort();

		if(key < 0)
			return;
		if(player.locking.locked(PacketType.KEY))
			return;

		switch(key) {

			case KeyEvent.VK_ESCAPE:
				if(player.settings.ESC_CLOSE) {
					if(!player.interfaceManager.isMainClear()) {
						player.interfaceManager.close();
						return;
					}
					player.send(new SendMessage("You have no interface currently opened."));
				}
				break;

			case KeyEvent.VK_SPACE:
				if(player.dialogueFactory.isActive()) {
					player.dialogueFactory.execute();
				}
				break;

			case KeyEvent.VK_1:
			case KeyEvent.VK_NUMPAD1:
				if(player.dialogueFactory.isActive()) {
					if(player.optionDialogue.isPresent()) {
						player.dialogueFactory.executeOption(0, player.optionDialogue);
						return;
					}
				}
				break;

			case KeyEvent.VK_2:
			case KeyEvent.VK_NUMPAD2:
				if(player.dialogueFactory.isActive()) {
					if(player.optionDialogue.isPresent()) {
						player.dialogueFactory.executeOption(1, player.optionDialogue);
						return;
					}
				}
				break;

			case KeyEvent.VK_3:
			case KeyEvent.VK_NUMPAD3:
				if(player.dialogueFactory.isActive()) {
					if(player.optionDialogue.isPresent()) {
						player.dialogueFactory.executeOption(2, player.optionDialogue);
						return;
					}
				}
				break;

			case KeyEvent.VK_4:
			case KeyEvent.VK_NUMPAD4:
				if(player.dialogueFactory.isActive()) {
					if(player.optionDialogue.isPresent()) {
						player.dialogueFactory.executeOption(3, player.optionDialogue);
						return;
					}
				}
				break;

			case KeyEvent.VK_5:
			case KeyEvent.VK_NUMPAD5:
				if(player.dialogueFactory.isActive()) {
					if(player.optionDialogue.isPresent()) {
						player.dialogueFactory.executeOption(4, player.optionDialogue);
						return;
					}
				}
				break;
		}
	}
}
