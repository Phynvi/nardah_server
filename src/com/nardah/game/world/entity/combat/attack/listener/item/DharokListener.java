package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;

/**
 * Handles the Dharok's armor effects to the assigned mob and item ids.
 * @author Michael | Chex
 */
@NpcCombatListenerSignature(npcs = {1673})
@ItemCombatListenerSignature(requireAll = true, items = {4716, 4718, 4720, 4722})
public class DharokListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyDamage(Actor attacker, Actor defender, int damage) {
		int health = attacker.getMaximumHealth() - attacker.getCurrentHealth();
		if(health < 0)
			health = 0;
		return (int) (damage + damage * 1.25 * health / 100);
	}

}
