package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {2216})
public class SergeantStrongstack extends SimplifiedListener<Mob> {

	@Override
	public void hit(Mob attacker, Actor defender, Hit hit) {
		if(!defender.isPlayer())
			return;
		if(Utility.random(10) != 0)
			return;

		Player playerDefender = defender.getPlayer();

		if(playerDefender.viewport.getPlayersInViewport().size() < 1)
			return;

		Player[] players = new Player[playerDefender.viewport.getPlayersInViewport().size()];
		int index = 0;

		for(Player player : playerDefender.viewport.getPlayersInViewport()) {
			if(attacker.getCombat().isAttacking(player))
				continue;
			if(!Utility.within(attacker, player, 7))
				continue;
			players[index] = player;
			index++;
		}

		Player next = Utility.randomElement(players);
		if(next != null)
			attacker.getCombat().attack(next);
	}
}
