package io.battlerune.game.world.entity.combat.attack.listener.item.voidknight;

import io.battlerune.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;

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
