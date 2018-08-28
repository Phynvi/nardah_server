package com.nardah.content.mysterybox;

import com.nardah.content.mysterybox.impl.*;
import com.nardah.game.world.items.Item;
import com.nardah.util.chance.Chance;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The mystery box class.
 * @author Daniel
 */
public abstract class MysteryBox {
	
	/**
	 * The map containing all the mystery boxes.
	 */
	private static Map<Integer, MysteryBox> MYSTERY_BOXES = new HashMap<>();
	
	/**
	 * Handles loading the mystery boxes.
	 */
	public static void load() {
		MysteryBox BRONZE_BOX = new BronzeMysteryBox();
		MYSTERY_BOXES.put(BRONZE_BOX.item(), BRONZE_BOX);
		/*MysteryBox SILVER_BOX = new SilverMysteryBox();
		MysteryBox GOLD_BOX = new GoldMysteryBox();
		MysteryBox GOD_SWORD_BOX = new GodswordMysteryBox();
		MysteryBox PET_BOX = new PetMysteryBox();
		MysteryBox RED_CRYSTAL = new RedCrystal();
		
		MYSTERY_BOXES.put(BRONZE_BOX.item(), BRONZE_BOX);
		MYSTERY_BOXES.put(SILVER_BOX.item(), SILVER_BOX);
		MYSTERY_BOXES.put(RED_CRYSTAL.item(), RED_CRYSTAL);
		MYSTERY_BOXES.put(GOLD_BOX.item(), GOLD_BOX);
		MYSTERY_BOXES.put(GOD_SWORD_BOX.item(), GOD_SWORD_BOX);
		MYSTERY_BOXES.put(PET_BOX.item(), PET_BOX);*/
	}
	
	/**
	 * Handles getting the mystery box.
	 */
	static Optional<MysteryBox> getMysteryBox(int item) {
		return MYSTERY_BOXES.containsKey(item) ? Optional.of(MYSTERY_BOXES.get(item)) : Optional.empty();
	}
	
	/**
	 * The name of the mystery box.
	 */
	protected abstract String name();
	
	/**
	 * The item identification of the mystery box.
	 */
	protected abstract int item();
	
	/**
	 * The amount considered for the item to be a rare item.
	 */
	protected abstract int rareValue();
	
	/**
	 * The rewards for the mystery box.
	 */
	protected abstract Chance<Item> rewards();
}
