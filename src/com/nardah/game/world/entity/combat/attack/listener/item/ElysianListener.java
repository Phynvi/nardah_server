package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Handles the Elysian spirit shield listener. OSRS Wiki:
 * http://oldschoolrunescape.wikia.com/wiki/Elysian_spirit_shield
 * @author Daniel
 */
@ItemCombatListenerSignature(requireAll = false, items = {12817})
public class ElysianListener extends SimplifiedListener<Player> {

	@Override
	public void block(Actor attacker, Player defender, Hit hit, CombatType combatType) {
		if(Math.random() > 0.30) {
			hit.modifyDamage(damage -> damage * 3 / 4);
			defender.graphic(new Graphic(321, UpdatePriority.HIGH));
		}
	}
}
