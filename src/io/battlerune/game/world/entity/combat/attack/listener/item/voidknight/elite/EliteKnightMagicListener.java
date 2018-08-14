package io.battlerune.game.world.entity.combat.attack.listener.item.voidknight.elite;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;

/**
 * Handles the slayer helmet / black mask effects ofr slayer tasks.
 * @author Michael | Chex
 */
@ItemCombatListenerSignature(requireAll = true, items = {11663, 13072, 13073, 8842})
public class EliteKnightMagicListener extends SimplifiedListener<Player> {

	@Override
	public int modifyMagicLevel(Player attacker, Actor defender, int level) {
		return level * 29 / 20;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int damage) {
		if(attacker.getStrategy().getCombatType() != CombatType.MAGIC)
			return damage;
		return damage * 41 / 40;
	}

}
