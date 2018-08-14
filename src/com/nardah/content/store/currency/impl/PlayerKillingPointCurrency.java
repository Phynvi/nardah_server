package com.nardah.content.store.currency.impl;

import com.nardah.content.store.currency.Currency;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

public final class PlayerKillingPointCurrency implements Currency {

	@Override
	public boolean tangible() {
		return false;
	}

	@Override
	public boolean takeCurrency(Player player, int amount) {
		if(player.pkPoints >= amount) {
			player.pkPoints -= amount;
			return true;
		} else {
			player.send(new SendMessage("You do not have enough pk points."));
			return false;
		}
	}

	@Override
	public void recieveCurrency(Player player, int amount) {
		player.pkPoints += amount;
	}

	@Override
	public int currencyAmount(Player player) {
		return player.pkPoints;
	}

	@Override
	public boolean canRecieveCurrency(Player player) {
		return true;
	}
}
