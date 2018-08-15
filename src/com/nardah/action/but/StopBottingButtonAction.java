package com.nardah.action.but;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.ButtonAction;
import com.nardah.game.Animation;
import com.nardah.game.world.entity.actor.player.Player;

public class StopBottingButtonAction extends ActionInitializer {
	
	@Override
	public void init() {
		ButtonAction action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				player.abortBot = true;
				player.interfaceManager.close();
				player.walkTo(player.getPosition());
				player.action.clearNonWalkableActions();
				player.resetAnimation();
				player.animate(new Animation(0), true);
				return true;
			}
		};
		action.register(-16028);
	}
}