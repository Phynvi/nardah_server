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
@ItemCombatListenerSignature(requireAll = true, items = {4708, 4710, 4712, 4714})
public class AhrimsListener extends SimplifiedListener<Actor> {

	@Override
	public int modifyDefensive(Actor attacker, Actor defender, int roll) {
		System.out.println("Roll is: " + roll);
		return roll / 2;
	}

	@Override
	public int modifyDefenceLevel(Actor attacker, Actor defender, int level) {
		System.out.println("Modifying defence level");
		return level * 2;
	}

	@Override
	public void hit(Actor attacker, Actor defender, Hit hit) {
		if(hit.getDamage() == 0) {
			hit.setDamage(RandomUtils.inclusive(0, 20));
		}
	}
}
