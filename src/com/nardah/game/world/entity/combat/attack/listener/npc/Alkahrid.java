package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {3103})
public class Alkahrid extends SimplifiedListener<Mob> {

	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		if(!attacker.isPlayer())
			return;

		int currentHealth = defender.getCurrentHealth();
		int maximumHealth = defender.getMaximumHealth();

		if(currentHealth == maximumHealth) {
			Player player = attacker.getPlayer();

			for(Mob monster : player.viewport.getNpcsInViewport()) {
				if(monster.id != 3103)
					continue;
				if(monster.equals(defender))
					continue;
				if(monster.getCombat().inCombat() && monster.getCombat().getDefender() != null)
					continue;
				if(!Utility.within(attacker.getPosition(), monster.getPosition(), 10))
					continue;
				monster.speak("Brother, I will help thee with this infidel!");
				monster.getCombat().attack(attacker);
			}
		}
	}
}
