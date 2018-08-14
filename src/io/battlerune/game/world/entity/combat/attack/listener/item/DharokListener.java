package io.battlerune.game.world.entity.combat.attack.listener.item;

import io.battlerune.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.mob.Mob;

/**
 * Handles the Dharok's armor effects to the assigned npc and item ids.
 * @author Michael | Chex
 */
@NpcCombatListenerSignature(npcs = {1673})
@ItemCombatListenerSignature(requireAll = true, items = {4716, 4718, 4720, 4722})
public class DharokListener extends SimplifiedListener<Mob> {

	@Override
	public int modifyDamage(Mob attacker, Mob defender, int damage) {
		int health = attacker.getMaximumHealth() - attacker.getCurrentHealth();
		if(health < 0)
			health = 0;
		return (int) (damage + damage * 1.25 * health / 100);
	}

}
