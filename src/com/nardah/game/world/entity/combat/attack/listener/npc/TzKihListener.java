package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {2189})
public class TzKihListener extends SimplifiedListener<Mob> {

	@Override
	public void hit(Mob attacker, Actor defender, Hit hit) {
		if(!defender.isPlayer())
			return;

		Player player = defender.getPlayer();
		int prayer = player.skills.get(Skill.PRAYER).getLevel();

		if(prayer - 1 < 0)
			return;

		player.skills.setLevel(Skill.PRAYER, prayer - 1);
	}
}
