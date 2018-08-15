package com.nardah.action.but;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.ButtonAction;
import com.nardah.game.world.entity.actor.player.Player;

public class SpecialAttackButtonAction extends ActionInitializer {
	
	@Override
	public void init() {
		ButtonAction action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				if (player.getCombatSpecial() != null) {
					if (!player.isSpecialActivated()) {
						player.getCombatSpecial().enable(player);
					} else {
						player.getCombatSpecial().disable(player, true);
					}
				}
				return true;
			}
		};
		action.register(1998);
	}
}
