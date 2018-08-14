package com.nardah.content.presetInterface;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendString;

public class PresetInterfaceHandler {

	/**
	 * Main Method, anything that is needed to appear on the interface should be
	 * called on here.
	 * @param player
	 */
	public void open(Player player) {
		player.inventory.refresh();
		player.equipment.refresh();
		sendStrings(player);
		player.interfaceManager.open(42500);

	}

	/**
	 * Send's the strings to the interface.
	 * @param player
	 */
	public void sendStrings(Player player) {
		player.send(new SendString("126 Melee", 42540, true));
		player.send(new SendString("126 Hybrid", 42541, true));
		player.send(new SendString("126 Tribrid", 42542, true));
		player.send(new SendString("Pure", 42543, true));
		player.send(new SendString("Pure tribrid", 42544, true));
		player.send(new SendString("Ranged Tank", 42545, true));
		player.send(new SendString("F2P", 42546, true));
		player.send(new SendString("Galvek Gear", 42547, true));
		player.send(new SendString("Runes", 42548, true));
	}

}
