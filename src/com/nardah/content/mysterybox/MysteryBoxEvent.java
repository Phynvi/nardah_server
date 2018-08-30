package com.nardah.content.mysterybox;

import java.util.*;
import java.util.stream.Collectors;

import com.nardah.game.task.TickableTask;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.*;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.util.chance.Chance;
import com.nardah.util.chance.WeightedChance;
import com.nardah.util.chance.WeightedObject;

public class MysteryBoxEvent extends TickableTask {
	
	private final Player player;
	private final MysteryBoxManager mysteryBox;
	private final List<WeightedObject<Item>> items = new ArrayList<>(11);
	
	
	private int result;


	MysteryBoxEvent(Player player) {
		super(false, 1);
		this.player = player;
		this.mysteryBox = player.mysteryBox;
	}

	private void reward() {
		WeightedObject<Item> reward = items.get(result);
		String boxName = mysteryBox.box.name();
		
		WeightedObject<Item> newItem = reward;
		if (reward.get().getAmount() > 1 && reward.get().isNoteable()) {
			newItem = new WeightedChance<>(reward.getWeight(), new Item(reward.get().getNotedId(), reward.get().getAmount()));
		}

		player.inventory.addOrBank(newItem.get());
		player.dialogueFactory.sendItem(boxName, "Congratulations, you have won " + Utility.getAOrAn(reward.get().getName()) + " " + reward.get().getName() + "!", reward.get().getId()).execute();
		player.send(new SendColor(59508, 0x37991C));

		byte type = getType(reward);
		if (type != 1) {
			World.sendMessage("<icon=17><col=5739B3> Nardah: <col=" + player.right.getColor() + ">" + player.getName() + " </col>has won " + Utility.getAOrAn(reward.get().getName()) + " <col=5739B3>" + reward.get().getName() + " </col>from the <col=5739B3>" + boxName + "</col>.");
		}

		player.locking.unlock();
	}

	@Override
	protected boolean canSchedule() {
		return player.mysteryBox.box != null;
	}

	@Override
	protected void onSchedule() {
		player.dialogueFactory.clear();
		player.locking.lock();
		
		player.inventory.remove(mysteryBox.box.item(), 1);
		mysteryBox.count = player.inventory.computeAmountForId(mysteryBox.box.item());
		Random rng = new Random();
		Set<Integer> generated = new LinkedHashSet<>();
		while (generated.size() <= 11)
		{
			Integer next = rng.nextInt(mysteryBox.box.rewards().size() -1);
			generated.add(next);
		}
		for (Integer slot : generated) {
			items.add(mysteryBox.box.rewards().get(slot));
		}
		
		//getting reward.
		WeightedObject<Item> res = mysteryBox.box.rewards().nextObject();
		result = RandomUtils.inclusive(0, 10);
		items.set(result, res);
		System.out.println("send: " + result);

		player.send(new SendColor(59508, 0xF01616));
		player.send(new SendString("You have " + mysteryBox.count + " mystery box available!", 59507));

	}

	private byte getType(WeightedObject<Item> item) {
		byte type = 1;
		if (item.getChanceType() == Chance.ChanceType.UNCOMMON) {
			type = 2;
		} else if (item.getChanceType()== Chance.ChanceType.RARE) {
			type = 3;
		} else if (item.getChanceType() == Chance.ChanceType.VERY_RARE) {
			type = 4;
		} else if (!item.get().isTradeable()) {
			type = 0;
		}
		return type;
	}

	@Override
	protected void tick() {
	
		if(tick == 0) {
			for(int index = 0; index < 11; index++) {
				if(index >= items.size())
					continue;
				WeightedObject<Item> item = items.get(index);
				player.send(new SendConfig((430 + index), getType(item)));
				player.send(new SendItemOnInterfaceSlot(59512, item.get(), index));
			}
			player.send(new SendMysteryBoxResult(result));
		}
		
		
		if(tick == 13)
			cancel();
	}

	@Override
	protected void onCancel(boolean logout) {
		if (logout) {
			player.inventory.add(mysteryBox.box.item(), 1);
		} else {
			reward();
		}
	}
}
