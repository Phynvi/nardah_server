package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.util.Utility;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

/**
 * Handles the Torag's armor effects to the assigned mob and item ids.
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {1676})
@ItemCombatListenerSignature(requireAll = true, items = {4745, 4747, 4749, 4751})
public class ToragListener extends SimplifiedListener<Actor> {

	@Override
	public void hit(Actor attacker, Actor defender, Hit hit) {
		if(defender.isPlayer() && hit.getDamage() > 1) {
			boolean success = Utility.random(100) <= 25;

			if(!success)
				return;

			Player player = defender.getPlayer();
			int energy = player.runEnergy;
			int drain = energy < 50 ? 10 : 20;

			energy -= drain;

			if(energy < 0)
				energy = 0;

			player.runEnergy = energy;
			player.send(new SendMessage(drain + "% run energy has been drained by " + attacker.getName() + "."));
			player.graphic(new Graphic(399, UpdatePriority.VERY_HIGH));

			if(attacker.isPlayer()) {
				attacker.getPlayer().send(new SendMessage("You have drained " + drain + "% of " + defender.getName() + "'s run energy."));
			}
		}

		super.hit(attacker, defender, hit);
	}
}
