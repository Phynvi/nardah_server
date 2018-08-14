package com.nardah.content.mysterybox.impl;

import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.game.world.items.Item;

/**
 * The gold (tier 3) mystery box.
 * @author Daniel
 */
public class GoldMysteryBox extends MysteryBox {
	@Override
	protected String name() {
		return "Gold mystery box";
	}

	@Override
	protected int item() {
		return 11739;
	}

	@Override
	protected int rareValue() {
		return 3_000_000;
	}

	@Override
	protected Item[] rewards() {
		return new Item[]{new Item(995, 10000000), // COINS
				new Item(19722, 1), // DRAGON_DEFENDER(T)

				new Item(11840, 1), // DRAGON_BOOTS
				new Item(11335, 1), // DRAGON_HELM
				new Item(20428, 1), // DRAGON_CHAIN
				new Item(11920, 1), // DRAGON_PICKAXE
				new Item(6739, 1), // DRAGON_AXE

				new Item(11907, 1), // TRIDENT_OF_THE_SEAS
				new Item(6914, 1), // MASTER_WAND

				new Item(6889, 1), // MAGE'S BOOK
				new Item(6918, 1), // INFINITY_HAT
				new Item(6916, 1), // INFINITY_TOP
				new Item(6924, 1), // INFINITY_BOTTOM
				new Item(6922, 1), // INFINITY_GLOVES
				new Item(6920, 1), // INFINITY_BOOTS

				new Item(4708, 1), // AHRIM'S_HOOD
				new Item(4710, 1), // AHRIM'S_STAFF
				new Item(4712, 1), // AHRIM'S_ROBETOP
				new Item(4714, 1), // AHRIM'S_ROBESKIRT
				new Item(4716, 1), // DHAROK'S_HELM
				new Item(4718, 1), // DHAROK'S_GREATAXE
				new Item(4720, 1), // DHAROK'S_PLATEBODY
				new Item(4722, 1), // DHAROK'_PLATELEGS
				new Item(4724, 1), // GUTHAN'S_HELM
				new Item(4726, 1), // GUTHAN'S_WARSPEAR
				new Item(4728, 1), // GUTHAN'S_PLATEBODY
				new Item(4730, 1), // GUTHAN'S_CHAINSKIRT
				new Item(4732, 1), // KARIL'S_COIF
				new Item(4734, 1), // KARIL'S_CROSSBOW
				new Item(4736, 1), // KARIL'S_LEATHERTOP
				new Item(4738, 1), // KARIL'S_LEATHERSKIRT
				new Item(4745, 1), // TORAG'S_HELM
				new Item(4747, 1), // TORAG'S_HAMMERS
				new Item(4749, 1), // TORAG'S_PLATEBODY
				new Item(4751, 1), // TORAG'S_PLATELEGS
				new Item(4753, 1), // VERAC'S_HELM
				new Item(4755, 1), // VERAC'S_FLAIL
				new Item(4757, 1), // VERAC'S_BRASSARD
				new Item(4759, 1), // VERAC'S_PLATESKIRT

				new Item(6731, 1), // SEERS_RING
				new Item(6733, 1), // ARCHERS_RING
				new Item(6735, 1), // WARRIOR_RING
				new Item(6737, 1), // BERSERKER_RING
				new Item(6729, 100), // DAGANNOTH_BONES
				new Item(536, 100), // DRAGON_BONES
				new Item(13265, 1), // ABYSSAL_DAGGER
				new Item(11791, 1), // STAFF_OF_THE_DEAD
				new Item(11824, 1), // ZAMORAKIAN_SPEAR
				new Item(11832, 1), // BANDOS_CHESTPLATE
				new Item(11834, 1), // BANDOS_TASSETS
				new Item(11836, 1), // BANDOS_BOOTS
				new Item(11826, 1), // ARMADYL_HELMET
				new Item(11828, 1), // ARMADYL_CHESTPLATE
				new Item(11830, 1), // ARMADYL_CHAINSKIRT
				new Item(11785, 1), // ARMADYL_CROSSBOW
				new Item(11838, 1), // SARADOMIN_SWORD
				new Item(11926, 1), // ODIUM_WARD
				new Item(11924, 1), // MALEDICTION_WARD
				new Item(11235, 1), // DARK_BOW
				new Item(4151, 1), // ABYSSAL_WHIP
				new Item(12924, 1), // TOXIC_BLOWPIPE
				new Item(6585, 1), // AMULET_OF_FURY
				new Item(11802, 1), // ARMADYL_GODSWORD
				new Item(11804, 1), // BANDOS_GODSWORD
				new Item(11806, 1), // SARADOMIN_GODSWORD
				new Item(11808, 1) // ZAMORAK_GODSWORD
		};
	}
}
