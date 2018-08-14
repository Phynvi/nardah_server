package com.nardah.content.mysterybox.impl;

import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.game.world.items.Item;

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
	protected Item[] rewards() {
		return new Item[]{new Item(995, 30000), // COINS
				new Item(392, 100), // MANTA_RAY
				new Item(1163, 1), // RUNE_FULL_HELM
				new Item(1127, 1), // RUNE_PLATEBODY
				new Item(1093, 1), // RUNE_PLATESKIRT
				new Item(1201, 1), // RUNE_KITESHIELD
				new Item(1079, 1), // RUNE_PLATELEGS
				new Item(1333, 1), // RUNE_SCIMITAR
				new Item(1319, 1), // RUNE_2HANDED_SWORD
				new Item(1187, 1), // DRAGON_SQ_SHIELD
				new Item(4587, 1), // DRAGON_SCIMITAR
				new Item(20000, 1), // DRAGON_SCIMITAR
				new Item(4087, 1), // DRAGON_PLATELEGS
				new Item(4585, 1), // DRAGON_PLATESKIRT
				new Item(3204, 1), // DRAGON_HALBERD
				new Item(1249, 1), // DRAGON_SPEAR
				new Item(1305, 1), // DRAGON_LONGSWORD
				new Item(1377, 1), // DRAGON_BATTLEAXE
				new Item(7158, 1), // DRAGON_2H_SWORD
				new Item(10828, 1), // HELM_OF_NEIT
				new Item(4153, 1), // GRANITE_MAUL
				new Item(3122, 1), // GRANITE_SHIELD
				new Item(10564, 1), // GRANITE_CHEST
				new Item(6528, 1), // TZHAAR_KET_OM
				new Item(4675, 1), // ANCIENT_STAFF
				new Item(4089, 1), // MYSTIC_HAT
				new Item(4091, 1), // MYSTIC_TOP
				new Item(4093, 1), // MYSTIC_BOTTOM
				new Item(4097, 1), // MYSTIC_HAT
				new Item(10551, 1), // FIGHTER_TORSO
				new Item(7462, 1), // BARROWS_GLOVES
				new Item(8850, 1), // RUNE_DEFENDER
				new Item(6570, 1), // FIRE_CAPE
				new Item(6571, 1), // UNCUT_ONYX
				new Item(4151, 1), // ABYSSAL_WHIP
				new Item(2577, 1) // RANGER_BOOT
		};
	}
}
