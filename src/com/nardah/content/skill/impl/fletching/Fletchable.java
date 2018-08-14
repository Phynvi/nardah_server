package com.nardah.content.skill.impl.fletching;

import com.nardah.game.world.items.Item;

public interface Fletchable {

	int getAnimation();

	Item getUse();

	Item getWith();

	FletchableItem[] getFletchableItems();

	Item[] getIngediants();

	String getProductionMessage();
}