package com.nardah.action.obj;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.ObjectAction;
import com.nardah.content.CrystalChest;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.GameObject;
import com.nardah.net.packet.out.SendMessage;

public class CrystalChestAction extends ActionInitializer {
	@Override
	public void init() {
		ObjectAction action = new ObjectAction() {
			@Override
			public boolean click(Player player, GameObject object, int click) {
				if (!player.inventory.contains(CrystalChest.KEY)) {
					player.dialogueFactory.sendItem("Crystal Key", "You need a crystal key to enter this chest!",
							CrystalChest.KEY.getId());
					player.send(new SendMessage("You need a crystal key to enter this chest!"));
					return true;
				}
				
				if (player.inventory.remaining() < 3) {
					player.send(new SendMessage("You need at lest 3 free inventory spaces to enter the chest."));
					return true;
				}
				
				player.action.execute(new com.nardah.game.action.impl.CrystalChestAction(player, object), true);
				return true;
			}
		};
		action.registerFirst(2191);
	}
}
