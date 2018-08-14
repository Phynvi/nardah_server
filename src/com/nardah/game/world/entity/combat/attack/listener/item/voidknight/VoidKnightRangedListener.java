package com.nardah.game.world.entity.combat.attack.listener.item.voidknight;

import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Handles the slayer helmet / black mask effects ofr slayer tasks.
 * @author Michael | Chex
 */
@ItemCombatListenerSignature(requireAll = true, items = {11664, 8839, 8840, 8842})
public class VoidKnightRangedListener extends SimplifiedListener<Player> {

	@Override
	public int modifyRangedLevel(Player attacker, Actor defender, int level) {
		return level * 11 / 10;
	}

}
