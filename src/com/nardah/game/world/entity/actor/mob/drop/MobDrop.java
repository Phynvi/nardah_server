package com.nardah.game.world.entity.actor.mob.drop;

import com.nardah.util.RandomGen;
import com.nardah.game.world.items.Item;

/**
 * The class which represents a single mob drop.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 29-1-2017.
 */
public final class MobDrop implements Comparable<MobDrop> {
	/**
	 * The item id of this drop.
	 */
	public int item;

	/**
	 * The chance this item is dropped.
	 */
	public MobDropChance type;

	/**
	 * The alternate chance modifier to use over the {@code chance} rarity.
	 */
	public final int chance;

	/**
	 * The minimum amount of this drop.
	 */
	public final int minimum;

	/**
	 * The maximum amount of this drop.
	 */
	public final int maximum;

	/**
	 * Constructs a new {@link MobDrop}.
	 */
	public MobDrop(int item, MobDropChance type, int chance, int minimum, int maximum) {
		this.item = item;
		this.type = type;
		this.chance = chance;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * Converts this {@link MobDrop} to an item.
	 */
	public Item toItem(RandomGen gen) {
		return new Item(item, gen.inclusive(minimum, maximum));
	}

	public void setItem(int item) {
		this.item = item;
	}

	public void setType(MobDropChance type) {
		this.type = type;
	}

	/**
	 * Determines if this chance will be successful or not.
	 */
	public boolean successful(RandomGen random) {
		int numerator = 1;
		int denominator = chance == 0 ? type.denominator : chance;
		return (random.inclusive(numerator, denominator)) % numerator == 0;
	}

	@Override
	public int compareTo(MobDrop other) {
		return type.ordinal() - other.type.ordinal();
	}
}
