package io.battlerune.content.store.currency.impl;

import io.battlerune.content.store.currency.Currency;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendMessage;

public final class SlayerPointCurrency implements Currency {
	
	@Override
	public boolean tangible() {
		return false;
	}
	
	@Override
	public boolean takeCurrency(Player player, int amount) {
		if(player.slayer.getPoints() >= amount) {
			player.slayer.setPoints(player.slayer.getPoints() - amount);
			return true;
		} else {
			player.send(new SendMessage("You do not have enough slayer points."));
			return false;
		}
	}
	
	@Override
	public void recieveCurrency(Player player, int amount) {
		player.slayer.setPoints(player.slayer.getPoints() + amount);
	}
	
	@Override
	public int currencyAmount(Player player) {
		return player.slayer.getPoints();
	}
	
	@Override
	public boolean canRecieveCurrency(Player player) {
		return true;
	}
}
