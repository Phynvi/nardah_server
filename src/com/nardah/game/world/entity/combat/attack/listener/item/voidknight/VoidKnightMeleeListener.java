package com.nardah.game.world.entity.combat.attack.listener.item.voidknight;

import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

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
