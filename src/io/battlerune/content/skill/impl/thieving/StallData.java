package io.battlerune.content.skill.impl.thieving;

import io.battlerune.game.world.items.Item;

import java.util.Arrays;
import java.util.Optional;

/**
 * Holds all the data for thieving stalls.
 * @author Daniel
 * @edited by Adam_#6723
 */
public enum StallData {
	PIZZA(11730, new Item(2291), 1, 25, 1250), FUR(11732, new Item(10127), 25, 50, 1500), SILK(11729, new Item(950), 40, 65, 2000), SILVER(11734, new Item(5011), 60, 80, 2500), SPICE(11733, new Item(21244), 75, 100, 3000), GOLD(11731, new Item(672), 90, 150, 3500);

	/**
	 * The object identification.
	 */
	private final int object;

	/**
	 * The item rewarded
	 */
	private final Item item;

	/**
	 * The level required
	 */
	private final int level;

	/**
	 * The experience rewarded.
	 */
	private final int experience;

	/**
	 * The item value.
	 */
	private final int value;

	/**
	 * Constructs a new <code>StallData<code>
	 */
	StallData(int object, Item item, int level, int experience, int value) {
		this.object = object;
		this.item = item;
		this.level = level;
		this.experience = experience;
		this.value = value;
	}

	/**
	 * Gets the object identification.
	 */
	public int getObject() {
		return object;
	}

	/**
	 * Gets the item reward.
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Gets the level required.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Gets the reward value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Gets the experience rewarded.
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * Gets the stall data base off object identification.
	 */
	public static Optional<StallData> forId(int id) {
		return Arrays.stream(values()).filter(a -> a.object == id).findAny();
	}

	/**
	 * Checks if the item is a reward.
	 */
	public static boolean isReward(Item item) {
		if(item == null)
			return false;
		return Arrays.stream(values()).anyMatch(i -> i.getItem().getId() == item.getId());
	}

	/**
	 * Gets the value of the reward.
	 */
	public static final int getValue(Item item) {
		return Arrays.stream(values()).filter(i -> i.getItem().getId() == item.getId()).findAny().get().getValue();
	}
}