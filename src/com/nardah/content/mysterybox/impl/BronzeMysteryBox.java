package com.nardah.content.mysterybox.impl;

import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.game.world.items.Item;
import com.nardah.util.chance.Chance;

/**
 * The bronze (tier 1) mystery box.
 * @author Aidan
 */
public class BronzeMysteryBox extends MysteryBox {
    @Override
    protected String name() {
        return "Bronze Mystery Box";
    }

    @Override
    protected int item() {
        return 6199;
    }

    @Override
    protected Chance<Item> rewards() {
        Chance<Item> rewards = new Chance<>();

        rewards.add(99, new Item(995, 2500000)); // COINS
        rewards.add(99, new Item(392, 100)); // MANTA_RAY
        rewards.add(95, new Item(12696, 20)); // SUPER_COMABT_POTION_(4)
        rewards.add(90, new Item(11840, 1)); // DRAGON_BOOTS
        rewards.add(90, new Item(11738, 5)); // HERB_BOX
        rewards.add(75, new Item(3140, 1)); // DRAGON_CHAINBODY
        rewards.add(75, new Item(2435, 50)); // PRAYER_POTION_(4)
        rewards.add(50, new Item(6739, 1)); // DRAGON_AXE
        rewards.add(45, new Item(6889, 1)); // MAGES_BOOK
        rewards.add(30, new Item(4207, 1)); // CRYSTAL_SEED
        rewards.add(25, new Item(537, 50)); // DRAGON_BONES
        rewards.add(25, new Item(10551, 1)); // FIGHTER_TORSO
        rewards.add(25, new Item(6570, 1)); // FIRE_CAPE
        rewards.add(15, new Item(12877, 1)); // DHAROKS_SET
        rewards.add(15, new Item(12881, 1)); // AHRIMS_SET
        rewards.add(15, new Item(12875, 1)); // VERACS_SET
        rewards.add(15, new Item(12873, 1)); // GUTHANS_SET
        rewards.add(15, new Item(12879, 1)); // TORAGS_SET
        rewards.add(15, new Item(12883, 1)); // KARILS_SET
        rewards.add(10, new Item(4151, 1)); // ABYSSAL_WHIP
        rewards.add(10, new Item(19722, 1)); // DRAGON_DEFENDER_(T)

        return rewards;
    }
}
