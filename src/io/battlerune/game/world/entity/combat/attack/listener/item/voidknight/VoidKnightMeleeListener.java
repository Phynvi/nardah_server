package io.battlerune.game.world.entity.combat.attack.listener.item.voidknight;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;

/**
 * Handles the slayer helmet / black mask effects ofr slayer tasks.
 * @author Michael | Chex
 */
@ItemCombatListenerSignature(requireAll = true, items = {11665, 8839, 8840, 8842})
public class VoidKnightMeleeListener extends SimplifiedListener<Player> {

	@Override
	public int modifyAttackLevel(Player attacker, Actor defender, int level) {
		if(attacker.getStrategy().getCombatType() != CombatType.MELEE)
			return level;
		return level * 11 / 10;
	}

	@Override
	public int modifyStrengthLevel(Player attacker, Actor defender, int level) {
		if(attacker.getStrategy().getCombatType() != CombatType.MELEE)
			return level;
		return level * 11 / 10;
	}

}
