package io.battlerune.game.world.entity.combat.attack.listener.npc;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.util.Utility;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {3103})
public class Alkahrid extends SimplifiedListener<Npc> {

	@Override
	public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		if(!attacker.isPlayer())
			return;

		int currentHealth = defender.getCurrentHealth();
		int maximumHealth = defender.getMaximumHealth();

		if(currentHealth == maximumHealth) {
			Player player = attacker.getPlayer();

			for(Npc monster : player.viewport.getNpcsInViewport()) {
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
