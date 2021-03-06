package com.nardah.content.mysterybox;

import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Area;
import com.nardah.net.packet.out.*;

import java.util.Optional;

public class MysteryBoxManager {
	private final Player player;
	int count;
	public MysteryBox box;
	
	public MysteryBoxManager(Player player) {
		this.player = player;
	}
	
	public boolean click(Item item) {
		Optional<MysteryBox> mBox = MysteryBox.getMysteryBox(item.getId());

		if(!mBox.isPresent()) {
			return false;
		}
		
		if(Area.inWilderness(player)) {
			player.dialogueFactory.sendStatement("You can not open a mystery box while in the wilderness!").execute();
			return true;
		}
		
		if(player.getCombat().inCombat()) {
			player.dialogueFactory.sendStatement("You can not open a mystery box while in combat!").execute();
			return true;
		}
		
		if(player.playerAssistant.busy() && !player.interfaceManager.isInterfaceOpen(59500)) {
			player.dialogueFactory.sendStatement("You can not open a mystery box right now!").execute();
			return true;
		}
		
		for(int index = 0; index < 11; index++) {
			player.send(new SendConfig((430 + index), 1));
		}
		
		box = mBox.get();
		count = player.inventory.computeAmountForId(item.getId());
		player.send(new SendItemOnInterface(59581, mBox.get().rewards().toItemArray()));
		player.send(new SendScrollbar(59580, mBox.get().rewards().size() * 5));
		player.send(new SendItemOnInterface(59512));
		player.send(new SendColor(59508, 0x37991C));
		player.send(new SendString(mBox.get().name(), 59503));
		player.send(new SendString("You have " + count + " available!", 59507));
		player.interfaceManager.open(59500);
		return true;
	}
	
	public void spin() {
		if(box == null) {
			player.interfaceManager.close();
			return;
		}
		if(count == 0) {
			player.dialogueFactory.sendStatement("You do not have any " + box.name() + "!", "To spin a different mystery box level click on the item first!").execute();
			return;
		}
		if(!player.interfaceManager.isInterfaceOpen(59500)) {
			return;
		}
		World.schedule(new MysteryBoxEvent(player));
	}
}
