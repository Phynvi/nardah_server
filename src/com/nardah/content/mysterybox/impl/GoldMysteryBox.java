package com.nardah.content.mysterybox.impl;

import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.game.world.items.Item;
import com.nardah.util.chance.Chance;

import static com.nardah.util.chance.Chance.ChanceType.*;

/**
 * The gold (tier 3) mystery box.
 *
 * @author Aidan
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
        return 100;
    }

    @Override
    protected Chance<Item> rewards() {
        Chance<Item> rewards = new Chance<>();

        rewards.add(COMMON, new Item(995, 10000000)); // COINS
        rewards.add(COMMON, new Item(12807, 1)); // ODIUM_WARD
        rewards.add(COMMON, new Item(12806, 1)); // MALEDICTION_WARD
        rewards.add(COMMON, new Item(13271, 1)); // ABYSSAL_DAGGER
        rewards.add(COMMON, new Item(12900, 1)); // TRIDENT_OF_THE_SWAMPS
        rewards.add(COMMON, new Item(12902, 1)); // TOXIC_STAFF_OF_THE_DEAD
        rewards.add(UNCOMMON, new Item(11286, 1)); // DRACONIC_VISAGE
        rewards.add(UNCOMMON, new Item(19493, 1)); // ZENYTE
//                rewards.add(UNCOMMON, new Item(xxx, 1)), // RING_CASE
        rewards.add(UNCOMMON, new Item(11834, 1)); // BANDOS_TASSETS
        rewards.add(UNCOMMON, new Item(11824, 1)); // ZAMORAKIAN_SPEAR
        rewards.add(UNCOMMON, new Item(12924, 1)); // TOXIC_BLOWPIPE
        rewards.add(RARE, new Item(21295, 1)); // INFERNAL CAPE
//                rewards.add(RARE, new Item(xxx, 1)), // GODSWORD_CASE
        rewards.add(RARE, new Item(11785, 1)); // ARMADYL_CROSSBOW
        rewards.add(RARE, new Item(13652, 1)); // DRAGON_CLAWS
        rewards.add(RARE, new Item(12825, 1)); // ARCANE_SPIRIT_SHIELD
        rewards.add(RARE, new Item(12821, 1)); // SPECTRAL_SPIRIT_SHIELD
        rewards.add(RARE, new Item(20785, 1)); // DRAGON_WARHAMMER
        rewards.add(RARE, new Item(21012, 1)); // DRAGON_HUNTER_CROSSBOW
        rewards.add(VERY_RARE, new Item(12817, 1)); // ELYSIAN_SPIRIT_SHIELD
        rewards.add(VERY_RARE, new Item(20997, 1)); // TWISTED_BOW
        rewards.add(VERY_RARE, new Item(962, 1)); // CHRISTMAS_CRACKER

        return rewards;
    }
}
