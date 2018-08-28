package com.nardah.content.mysterybox.impl;

import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.game.world.items.Item;
import com.nardah.util.chance.Chance;

import static com.nardah.util.chance.Chance.ChanceType.*;

/**
 * The silver (tier 2) mystery box.
 *
 * @author Daniel
 */
public class SilverMysteryBox extends MysteryBox {
    @Override
    protected String name() {
        return "Silver mystery box";
    }

    @Override
    protected int item() {
        return 12955;
    }

    @Override
    protected int rareValue() {
        return 1_500_000;
    }

    @Override
    protected Chance<Item> rewards() {
        Chance<Item> rewards = new Chance<>();

        rewards.add(COMMON, new Item(995, 5000000)); // COINS
        rewards.add(COMMON, new Item(12934, 2500)); // ZULRAH_SCALES
        rewards.add(COMMON, new Item(2551, 50)); // RING_OF_RECOIL
        rewards.add(COMMON, new Item(21028, 1)); // DRAGON_HARPOON
        rewards.add(COMMON, new Item(12809, 1)); // BLESSED_SARADOMIN_SWORD
        rewards.add(COMMON, new Item(11235, 1)); // DARK_BOW
        rewards.add(COMMON, new Item(21279, 1)); // OBSIDIAN_ARMOR_SET
        rewards.add(UNCOMMON, new Item(11905, 1)); // FULL_TRIDENT
        rewards.add(UNCOMMON, new Item(6585, 1)); // AMULET_OF_FURY
        rewards.add(UNCOMMON, new Item(11836, 1)); // BANDOS_BOOTS
        rewards.add(UNCOMMON, new Item(10926, 100)); // SANFEW_SERUM_(4)
        rewards.add(UNCOMMON, new Item(12004, 1)); // KRAKEN_TENTACLE
        rewards.add(UNCOMMON, new Item(11791, 1)); // STAFF_OF_THE_DEAD
        rewards.add(UNCOMMON, new Item(12932, 1)); // MAGIC FANG
        rewards.add(RARE, new Item(11920, 1)); // DRAGON_PICKAXE
        rewards.add(RARE, new Item(6920, 1)); // INFINITY_BOOTS
        rewards.add(RARE, new Item(19481, 1)); // HEAVY_BALLISTA
        rewards.add(RARE, new Item(12929, 1)); // SERPENTINE_VISAGE
        rewards.add(RARE, new Item(2577, 1)); // RANGER_BOOTS
        rewards.add(RARE, new Item(12831, 1)); // BLESSED_SPIRIT_SHIELD
        rewards.add(RARE, new Item(11826, 1)); // ARMADYL_HELMET
        rewards.add(VERY_RARE, new Item(11828, 1)); // ARMADYL_CHESTPLATE
        rewards.add(VERY_RARE, new Item(11830, 1)); // ARMADYL_CHAINSKIRT

        return rewards;
    }
}
