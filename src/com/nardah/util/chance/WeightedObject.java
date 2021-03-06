package com.nardah.util.chance;

/**
 * Represents a weighted object.
 * @author Michael | Chex
 */
public interface WeightedObject<T> extends Comparable<WeightedObject<T>> {
	
	/**
	 * Gets the object's weight.
	 */
	double getWeight();
	
	/**
	 * Gets the representation of the weighted chance.
	 */
	T get();

	default Chance.ChanceType getChanceType() {
		return Chance.ChanceType.ALWAYS;
	}
	
	@Override
	String toString();
}
