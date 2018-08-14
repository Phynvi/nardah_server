package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

/**
 * Handles the Occult necklace listener. OSRS Wiki:
 * http://oldschoolrunescape.wikia.com/wiki/Occult_necklace
 * @author Daniel
 */
@ItemCombatListenerSignature(requireAll = false, items = {12002, 19720})
public class OccultNecklaceListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyDamage(Actor attacker, Actor defender, int damage) {
		if(attacker.getStrategy().getCombatType() != CombatType.MAGIC)
			return damage;
		return damage * 11 / 10;
	}

}
