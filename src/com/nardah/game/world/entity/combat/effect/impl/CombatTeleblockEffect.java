package com.nardah.game.world.entity.combat.effect.impl;

import com.nardah.game.world.entity.combat.effect.CombatEffect;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendWidget;

/**
 * The combat effect applied when a player needs to be teleblocked.
 * @author lare96 <http://github.com/lare96>
 */
public final class CombatTeleblockEffect extends CombatEffect {

	/**
	 * Creates a new {@link CombatTeleblockEffect}.
	 */
	public CombatTeleblockEffect() {
		super(50);
	}

	@Override
	public boolean apply(Actor actor) {
		if(actor.isPlayer()) {
			Player player = (Player) actor;
			if(player.isTeleblocked()) {
				return false;
			}

			if(player.prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
				player.teleblock(250);
				player.send(new SendWidget(SendWidget.WidgetType.TELEBLOCK, 150));
			} else {
				player.teleblock(500);
				player.send(new SendWidget(SendWidget.WidgetType.TELEBLOCK, 300));
			}

			player.send(new SendMessage("You have just been tele-blocked!"));
			return true;
		}
		return false;
	}

	@Override
	public boolean removeOn(Actor actor) {
		return false;
	}

	@Override
	public void process(Actor actor) {
	}

	@Override
	public boolean onLogin(Actor actor) {
		if(actor.isPlayer()) {
			Player player = (Player) actor;
			if(player.isTeleblocked()) {
				return true;
			}
		}
		return false;
	}
}
