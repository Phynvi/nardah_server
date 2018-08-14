package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Handles the slayer helmet / black mask effects ofr slayer tasks.
 * @author Michael | Chex
 */
@ItemCombatListenerSignature(requireAll = false, items = {
		/* Slayer helmets */
		11864, 19639, 19643, 19647, 21264,

		/* Black masks */
		8901, 8903, 8905, 8907, 8909, 8911, 8913, 8915, 8917, 8919, 8921})
public class SlayerHelmListener extends SimplifiedListener<Player> {

	@Override
	public int modifyAttackLevel(Player attacker, Actor defender, int level) {
		if(attacker.slayer.getTask() != null && attacker.slayer.getTask().valid(defender.id))
			return level * 7 / 6;
		return level;
	}

	@Override
	public int modifyStrengthLevel(Player attacker, Actor defender, int level) {
		if(attacker.slayer.getTask() != null && attacker.slayer.getTask().valid(defender.id))
			return level * 7 / 6;
		return level;
	}

}
