package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.game.world.position.Area;
import com.nardah.Config;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

/**
 * Handles the ring of life listener. OSRS Wiki:
 * http://oldschoolrunescape.wikia.com/wiki/Ring_of_life
 * @author Daniel
 */
@ItemCombatListenerSignature(requireAll = false, items = {2570})
public class RingOfLifeListener extends SimplifiedListener<Player> {

	@Override
	public void block(Actor attacker, Player defender, Hit hit, CombatType combatType) {
		if(Area.inDuelArena(defender))
			return;
		if(defender.getCurrentHealth() - hit.getDamage() <= 0)
			return;
		if(defender.getCurrentHealth() - hit.getDamage() <= defender.getMaximumHealth() * 0.10) {
			if(Teleportation.teleport(defender, Config.DEFAULT_POSITION)) {
				defender.send(new SendMessage("The Ring of life has saved you; but was destroyed in the process."));
			}
			defender.getCombat().removeListener(this);
			defender.equipment.remove(2570);
		}
	}
}
