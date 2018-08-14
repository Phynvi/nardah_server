package com.nardah.game.world.entity.combat.attack.listener.other;

import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.HitIcon;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * no more bugs
 */
public class VengeanceListener extends SimplifiedListener<Player> {

	private static final VengeanceListener INSTANCE = new VengeanceListener();

	private VengeanceListener() {
	}

	@Override
	public void block(Actor attacker, Player defender, Hit hit, CombatType combatType) {
		if(hit.getDamage() < 2) {
			return;
		}

		Hit recoil = new Hit((int) (hit.getDamage() * 0.75), HitIcon.DEFLECT);
		attacker.damage(recoil);
		attacker.getCombat().getDamageCache().add(defender, recoil);
		defender.speak("Taste vengeance!");
		defender.venged = false;
	}

	@Override
	public void finishIncoming(Actor attacker, Player defender) {
		if(!defender.venged) {
			defender.getCombat().removeListener(this);
		}
	}

	public static VengeanceListener get() {
		return INSTANCE;
	}

}
