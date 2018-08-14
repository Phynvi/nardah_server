package io.battlerune.game.world.entity.combat.attack.listener.item;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.HitIcon;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.containers.equipment.Equipment;
import io.battlerune.net.packet.out.SendMessage;

/**
 * @author Adam_#6723
 */
@NpcCombatListenerSignature(npcs = {2192})
@ItemCombatListenerSignature(requireAll = false, items = {2550})
public class RingOfRecoilListener extends SimplifiedListener<Actor> {

	@Override
	public void block(Actor attacker, Actor defender, Hit hit, CombatType combatType) {
		if(hit.getDamage() < 1)
			return;

		int recoil = hit.getDamage() < 10 ? 1 : hit.getDamage() / 10;

		if(defender.isNpc() || defender.isPlayer()) {
			handleRecoil(attacker, defender, recoil);
			return;
		}

		Player player = defender.getPlayer();
		int charges = player.ringOfRecoil;
		charges -= recoil;

		if(charges <= 0) {
			player.getCombat().removeListener(this);
			player.send(new SendMessage("Your ring of recoil has shattered!"));
			player.equipment.set(Equipment.RING_SLOT, null, true);
			recoil += charges;
			charges = 40;
		}

		player.ringOfRecoil = charges;
		if(recoil > 0)
			handleRecoil(attacker, defender, recoil);
	}

	private void handleRecoil(Actor attacker, Actor defender, int recoil) {
		Hit hit = new Hit(recoil, HitIcon.DEFLECT);
		attacker.damage(hit);
		attacker.getCombat().getDamageCache().add(defender, hit);
	}
}
