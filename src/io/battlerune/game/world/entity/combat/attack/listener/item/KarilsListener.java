package io.battlerune.game.world.entity.combat.attack.listener.item;

import io.battlerune.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.util.RandomUtils;

/**
 * @author red
 */
@NpcCombatListenerSignature(npcs = {1674})
@ItemCombatListenerSignature(requireAll = true, items = {4732, 4734, 4736, 4738})
public class KarilsListener extends SimplifiedListener<Actor> {

	@Override
	public void hit(Actor attacker, Actor defender, Hit hit) {
		if(hit.getDamage() == 0) {
			hit.setDamage(RandomUtils.inclusive(0, 20));
		}
	}
}
