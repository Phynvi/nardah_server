package com.nardah.content.skill.impl.magic.spell;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;

/**
 * The itemcontainer for casting a spell.
 * @author Daniel
 */
public interface Spell {

	/**
	 * Gets the name of the spell.
	 */
	String getName();

	/**
	 * Gets the runes required to cast the spell.
	 */
	Item[] getRunes();

	/**
	 * Gets the level required to cast spell.
	 */
	int getLevel();

	/**
	 * Executes the magic spell.
	 */
	void execute(Player player, Item item);

}
