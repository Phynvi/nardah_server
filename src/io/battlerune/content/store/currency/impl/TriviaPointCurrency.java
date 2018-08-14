package io.battlerune.content.store.currency.impl;

import io.battlerune.content.store.currency.Currency;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendMessage;

public final class TriviaPointCurrency implements Currency {

	@Override
	public boolean tangible() {
		return false;
	}

	@Override
	public boolean takeCurrency(Player player, int amount) {
		if(player.triviaPoints >= amount) {
			player.triviaPoints -= amount;
			return true;
		} else {
			player.send(new SendMessage("You do not have enough Boss Points."));
			return false;
		}
	}

	@Override
	public void recieveCurrency(Player player, int amount) {
		player.triviaPoints += amount;
	}

	@Override
	public int currencyAmount(Player player) {
		return player.triviaPoints;
	}

	@Override
	public boolean canRecieveCurrency(Player player) {
		return true;
	}
}
