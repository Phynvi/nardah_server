package com.nardah.content.mysterybox.impl;

import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.game.world.items.Item;
import com.nardah.util.chance.Chance;

/**
 * The gold (tier 3) mystery box.
 * @author Aidan
 */
public class GoldMysteryBox extends MysteryBox {
    @Override
    protected String name() {
        return "Gold Mystery Box";
    }

    @Override
    protected int item() {
        return 11739;
    }

    @Override
    protected Chance<Item> rewards() {
        Chance<Item> rewards = new Chance<>();

        rewards.add(99, new Item(995, 10000000)); // COINS
        rewards.add(95, new Item(12807, 1)); // ODIUM_WARD
        rewards.add(95, new Item(12806, 1)); // MALEDICTION_WARD
        rewards.add(90, new Item(13271, 1)); // ABYSSAL_DAGGER
        rewards.add(85, new Item(12900, 1)); // TRIDENT_OF_THE_SWAMPS
        rewards.add(85, new Item(12902, 1)); // TOXIC_STAFF_OF_THE_DEAD
        rewards.add(75, new Item(11286, 1)); // DRACONIC_VISAGE
        rewards.add(70, new Item(19493, 1)); // ZENYTE
//        rewards.add(70, new Item(xxx, 1)); // RING_CASE
        rewards.add(65, new Item(11834, 1)); // BANDOS_TASSETS
        rewards.add(65, new Item(11824, 1)); // ZAMORAKIAN_SPEAR
        rewards.add(50, new Item(12924, 1)); // TOXIC_BLOWPIPE
        rewards.add(30, new Item(21295, 1)); // INFERNAL CAPE
//        rewards.add(25, new Item(xxx, 1)); // GODSWORD_CASE
        rewards.add(25, new Item(11785, 1)); // ARMADYL_CROSSBOW
        rewards.add(20, new Item(13652, 1)); // DRAGON_CLAWS
        rewards.add(10, new Item(12825, 1)); // ARCANE_SPIRIT_SHIELD
        rewards.add(10, new Item(12821, 1)); // SPECTRAL_SPIRIT_SHIELD
        rewards.add(5, new Item(20785, 1)); // DRAGON_WARHAMMER
        rewards.add(5, new Item(21012, 1)); // DRAGON_HUNTER_CROSSBOW
        rewards.add(1, new Item(12817, 1)); // ELYSIAN_SPIRIT_SHIELD
        rewards.add(1, new Item(20997, 1)); // TWISTED_BOW
        rewards.add(.5, new Item(962, 1)); // CHRISTMAS_CRACKER

        return rewards;
    }
}

