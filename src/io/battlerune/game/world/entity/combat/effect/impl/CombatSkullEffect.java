package io.battlerune.game.world.entity.combat.effect.impl;

import io.battlerune.game.world.entity.combat.effect.CombatEffect;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Area;

/**
 * The combat effect applied when a player needs to be skulled.
 * @author lare96 <http://github.com/lare96>
 */
public final class CombatSkullEffect extends CombatEffect {

	/**
	 * Creates a new {@link CombatSkullEffect}.
	 */
	public CombatSkullEffect() {
		super(50);
	}

	@Override
	public boolean apply(Actor actor) {
		if(actor.isPlayer()) {
			Player player = (Player) actor;

			if(Area.inEventArena(actor)) {
				return false;
			}

			if(!Area.inWilderness(actor)) {
				return false;
			}

			if(player.skulling.isSkulled()) {
				return false;
			}

			player.skulling.skull();
			return true;
		}
		return false;
	}

	@Override
	public boolean removeOn(Actor actor) {
		if(actor.isPlayer()) {
			Player player = (Player) actor;

			if(!player.skulling.isSkulled()) {
				player.skulling.unskull();
				return true;
			}

			return false;
		}

		return true;
	}

	@Override
	public void process(Actor actor) {
		// nothing to process
	}

	@Override
	public boolean onLogin(Actor actor) {
		if(actor.isPlayer()) {
			Player player = (Player) actor;

			if(player.skulling.isSkulled()) {
				return true;
			}
		}
		return false;
	}
}
