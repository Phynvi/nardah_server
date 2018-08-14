package com.nardah.content.activity.impl.flowerpoker;

/**
 * Stores flower item- and object id
 * @author Harryl / Nerik#8690
 */
public enum FlowerData {

	PASTEL_FLOWERS(2980, 2460), RED_FLOWERS(2981, 2462), BLUE_FLOWERS(2982, 2464), YELLOW_FLOWERS(2983, 2466), PURPLE_FLOWERS(2984, 2468), ORANGE_FLOWERS(2985, 2470), RAINBOW_FLOWERS(2986, 2472),

	WHITE_FLOWERS(2987, 2474), BLACK_FLOWERS(2988, 2476);

	private int objectId;
	private int itemId;

	FlowerData(int objectId, int itemId) {
		this.objectId = objectId;
		this.itemId = itemId;
	}

	public int getObjectId() {
		return objectId;
	}

	public int getItemId() {
		return itemId;
	}

	public FlowerData forId(int id) {
		for(FlowerData data : FlowerData.values()) {
			if(data.getItemId() == id) {
				return data;
			}
		}
		return null;
	}
}
