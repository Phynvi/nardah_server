package com.nardah.action.mob;

import java.util.HashMap;
import java.util.Map;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.MobAction;
import com.nardah.content.consume.PotionData;
import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.game.event.impl.NpcClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.util.Utility;

public class DecantMobAction extends ActionInitializer {

	private final static int COST = 150_000;

	private void decant(Player player) {
		if (!player.inventory.contains(995, COST)) {
			player.dialogueFactory.sendStatement("You do not have enough coins to do this!");
			return;
		}

		player.inventory.remove(995, COST);
		decantAll(player);
	}
	
	@Override
	public void init() {
		MobAction action = new MobAction() {
			@Override
			public boolean click(Player player, Mob mob, int click) {
				DialogueFactory factory = player.dialogueFactory;
				if (!canDecant(player)) {
					factory.sendNpcChat(1146, "Hello, I am Joe the chemist.", "You don't have any potions for me to decant!",
							"Bye.").execute();
					return true;
				}
				factory.sendNpcChat(1146, "Hello, I am Joe the chemist.", "How may I help you?");
				factory.sendOption("Decant all potions (" + Utility.formatDigits(COST) + " coins)",
						() -> factory.onAction(() -> decant(player))
								.sendStatement("Joe the Chemist has successfully decanted all your",
										"positions for " + Utility.formatDigits(COST) + " coins.")
								.onNext(),
						
						"Nevermind", () -> factory.onAction(player.interfaceManager::close));
				factory.execute();
				return true;
			}
		};
		action.registerFirst(1146);
	}

	private boolean canDecant(Player player) {
		Map<PotionData, Integer> doses = new HashMap<>();

		int emptyVials = 0;
		for (int index = 0; index < player.inventory.capacity(); index++) {
			Item item = player.inventory.get(index);

			if (item == null) {
				continue;
			}

			PotionData potion = PotionData.forId(item.getId()).orElse(null);

			if (potion == null) {
				continue;
			}

			emptyVials++;
			doses.computeIfPresent(potion, (data, dosage) -> dosage += getDoses(item.getId()));
			doses.computeIfAbsent(potion, data -> getDoses(item.getId()));
		}

		for (Map.Entry<PotionData, Integer> potion : doses.entrySet()) {
			int fullPotions = potion.getValue() / 4;
			int remainder = potion.getValue() - fullPotions * 4;
			emptyVials -= fullPotions;

			if (remainder > 0) {
				emptyVials--;
			}
		}
		return emptyVials > 0;
	}

	private void decantAll(Player player) {
		Map<PotionData, Integer> doses = new HashMap<>();

		int emptyVials = 0;
		for (int index = 0; index < player.inventory.capacity(); index++) {
			Item item = player.inventory.get(index);

			if (item == null) {
				continue;
			}

			PotionData potion = PotionData.forId(item.getId()).orElse(null);

			if (potion == null) {
				continue;
			}

			doses.computeIfPresent(potion, (data, dosage) -> dosage += getDoses(item.getId()));
			doses.computeIfAbsent(potion, data -> getDoses(item.getId()));
			player.inventory.remove(item, index, false);
			emptyVials++;
		}

		for (Map.Entry<PotionData, Integer> potion : doses.entrySet()) {
			int fullPotions = potion.getValue() / 4;
			int remainder = potion.getValue() - fullPotions * 4;
			emptyVials -= fullPotions;

			player.inventory.add(new Item(potion.getKey().getIds()[0], fullPotions), -1, false);

			if (remainder > 0) {
				emptyVials--;
				player.inventory.add(new Item(potion.getKey().getIdForDose(remainder)), -1, false);
			}
		}

		player.inventory.add(new Item(229, emptyVials), -1, false);
		player.inventory.refresh();
	}

	private static int getDoses(int id) {
		ItemDefinition definition = ItemDefinition.get(id);
		int index = definition.getName().lastIndexOf(')');
		return Integer.valueOf(definition.getName().substring(index - 1, index));
	}
	
}
