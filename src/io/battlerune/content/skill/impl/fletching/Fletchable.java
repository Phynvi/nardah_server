package io.battlerune.content.skill.impl.fletching;

import io.battlerune.game.world.items.Item;

public interface Fletchable {

	int getAnimation();

	Item getUse();

	Item getWith();

	FletchableItem[] getFletchableItems();

	Item[] getIngediants();

	String getProductionMessage();
}