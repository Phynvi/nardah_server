package com.nardah.util.chance;

import java.util.Objects;

/**
 * An item with a common chance.
 * @author Michael | Chex
 */
public class WeightedChance<T> implements WeightedObject<T> {
	
	/**
	 * The representation.
	 */
	private final T representation;
	
	/**
	 * The weight type.
	 */
	private double weight;

	private Chance.ChanceType type;
	
	public WeightedChance(double weight, T representation) {
		if(weight <= 0) {
			throw new IllegalArgumentException("The weight of an item must be larger than zero!");
		}
		this.representation = Objects.requireNonNull(representation);
		this.weight = weight;
	}

	public WeightedChance(double weight, Chance.ChanceType type, T representation) {
		if(weight <= 0) {
			throw new IllegalArgumentException("The weight of an item must be larger than zero!");
		}
		this.representation = Objects.requireNonNull(representation);
		this.weight = weight;
		this.type = type;
	}

	@Override
	public double getWeight() {
		return weight;
	}
	
	@Override
	public T get() {
		return representation;
	}

	@Override
	public Chance.ChanceType getChanceType() {
		return type;
	}

	@Override
	public int compareTo(WeightedObject<T> o) {
		return (int) (getWeight() - o.getWeight());
	}
	
	@Override
	public String toString() {
		return "[Object: " + get() + ", " + "Weight: " + getWeight() + "]";
	}
}
