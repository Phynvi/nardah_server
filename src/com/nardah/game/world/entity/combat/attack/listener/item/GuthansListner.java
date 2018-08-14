package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

/**
 * Handles the guthan item set listener OSRS Wiki:
 * http://oldschoolrunescape.wikia.com/wiki/Guthan_the_Infested%27s_equipment
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {1674})
@ItemCombatListenerSignature(requireAll = true, items = {4726, 4724, 4728, 4730})
public class GuthansListner extends SimplifiedListener<Actor> {

	@Override
	public void hit(Actor attacker, Actor defender, Hit hit) {
		if(Math.random() > 0.50) {
			attacker.heal(hit.getDamage());
			attacker.graphic(new Graphic(398, UpdatePriority.HIGH));
		}
	}
}
