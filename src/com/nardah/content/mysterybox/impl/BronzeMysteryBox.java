package com.nardah.content.mysterybox.impl;

import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.game.world.items.Item;
import com.nardah.util.chance.Chance;
import static com.nardah.util.chance.Chance.ChanceType.*;
import com.nardah.util.chance.WeightedChance;

import java.util.Arrays;
import java.util.List;

/**
 * The bronze (tier 1) mystery box.
 * @author Daniel
 */
public class BronzeMysteryBox extends MysteryBox {
	@Override
	protected String name() {
		return "Bronze mystery box";
	}

	@Override
	protected int item() {
		return 6199;
	}

	@Override
	protected int rareValue() {
		return 1_000_000;
	}

	@Override
	protected Chance<Item> rewards() {
		Chance<Item> rewards = new Chance<>();

		rewards.add(ALWAYS, new Item(392, 100)); // MANTA_RAY
		
		rewards.add(ALWAYS, new Item(392, 100)); // MANTA_RAY
		rewards.add(ALWAYS, new Item(12696, 20)); // SUPER_COMABT_POTION_(4)
		rewards.add(ALWAYS, new Item(4207, 1)); // CRYSTAL_SEED
		rewards.add(ALWAYS,new Item(10551, 1)); // FIGHTER_TORSO
		rewards.add(ALWAYS,new Item(6570, 1)); // FIRE_CAPE
		rewards.add(ALWAYS,new Item(4151, 1)); // ABYSSAL_WHIP
		rewards.add(ALWAYS,new Item(11840, 1)); // DRAGON_BOOTS
		rewards.add(ALWAYS,new Item(12877, 1)); // DHAROKS_SET
		rewards.add(ALWAYS,new Item(12881, 1)); // AHRIMS_SET
		rewards.add(ALWAYS,new Item(12875, 1)); // VERACS_SET
		rewards.add(ALWAYS,new Item(12873, 1)); // GUTHANS_SET
		rewards.add(ALWAYS,new Item(12879, 1)); // TORAGS_SET
		rewards.add(ALWAYS,new Item(12883, 1)); // KARILS_SET
		rewards.add(ALWAYS,new Item(11738, 5)); // HERB_BOX
		rewards.add(ALWAYS,new Item(537, 50)); // DRAGON_BONES
		rewards.add(ALWAYS,new Item(3140, 1)); // DRAGON_CHAINBODY
		rewards.add(ALWAYS,new Item(6739, 1)); // DRAGON_AXE
		rewards.add(ALWAYS,new Item(6889, 1)); // MAGES_BOOK
		rewards.add(ALWAYS,new Item(19722, 1)); // DRAGON_DEFENDER_(T)
		rewards.add(ALWAYS,new Item(2435, 50)); // PRAYER_POTION_(4)

		return rewards;
	}

	/*@Override
	protected Item[] rewards() {
		return new Item[]{new Item(995, 2500000), // COINS
				new Item(392, 100), // MANTA_RAY
				new Item(12696, 20), // SUPER_COMABT_POTION_(4)
				new Item(4207, 1), // CRYSTAL_SEED
				new Item(10551, 1), // FIGHTER_TORSO
				new Item(6570, 1), // FIRE_CAPE
				new Item(4151, 1), // ABYSSAL_WHIP
				new Item(11840, 1), // DRAGON_BOOTS
				new Item(12877, 1), // DHAROKS_SET
				new Item(12881, 1), // AHRIMS_SET
				new Item(12875, 1), // VERACS_SET
				new Item(12873, 1), // GUTHANS_SET
				new Item(12879, 1), // TORAGS_SET
				new Item(12883, 1), // KARILS_SET
				new Item(11738, 5), // HERB_BOX
				new Item(537, 50), // DRAGON_BONES
				new Item(3140, 1), // DRAGON_CHAINBODY
				new Item(6739, 1), // DRAGON_AXE
				new Item(6889, 1), // MAGES_BOOK
				new Item(19722, 1), // DRAGON_DEFENDER_(T)
				new Item(2435, 50), // PRAYER_POTION_(4)
		};
	}*/
}
