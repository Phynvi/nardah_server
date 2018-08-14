package com.nardah.content.itemaction.impl;

import com.nardah.content.clanchannel.ClanUtility;
import com.nardah.content.clanchannel.content.ClanLevel;
import com.nardah.content.clanchannel.channel.ClanChannel;
import com.nardah.content.itemaction.ItemAction;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ClanShowcaseBox extends ItemAction {

	@Override
	public String name() {
		return "Clan Showcase Box";
	}

	@Override
	public boolean inventory(Player player, Item item, int opcode) {
		if(opcode != 1) {
			return false;
		}
		ClanChannel channel = player.clanChannel;
		if(channel == null) {
			player.send(new SendMessage("You need to be in a clan to do this!"));
			return true;
		}
		if(channel.getShowcase().showcaseItems.size() >= 28) {
			player.send(new SendMessage("You have reached the maximum capacity of showcase items you can hold. Please delete an item to proceed."));
			return true;
		}
		ClanLevel level = channel.getDetails().level;
		List<Item> items = new ArrayList<>();

		for(int reward : ClanUtility.getRewardItems(level)) {
			Item rewardItem = new Item(reward, 1);
			for(Item showcase : channel.getShowcaseItems()) {
				if(rewardItem.getId() != showcase.getId())
					items.add(rewardItem);
			}
		}

		if(items.isEmpty()) {
			return true;
		}

		Item showcaseReward = Utility.randomElement(items);
		player.inventory.remove(item);
		channel.getShowcase().showcaseItems.add(showcaseReward.getId());
		channel.message("We just received " + showcaseReward.getName() + " from the showcase box!");
		return true;
	}
}
