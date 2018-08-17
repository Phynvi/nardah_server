package com.nardah.action.but;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.ButtonAction;
import com.nardah.content.teleport.TeleportHandler;
import com.nardah.game.world.entity.actor.player.Player;

public class TeleportButtonAction extends ActionInitializer {

	@Override
	public void init() {
		ButtonAction action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.open(player);
				return true;
			}
		};
		action.register(850);
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.teleport(player);
				return true;
			}
		};
		action.register(-7501);
		
		/* TODO: ETHAN FINISH
		if (button >= -7484 && button <= -7440) {
			TeleportHandler.click(player, button);
			return true;
		}
		switch (button) {
		case -7530:
			TeleportHandler.open(player, TeleportType.FAVORITES);
			return true;
		case 1541:
		case 13079:
		case -7526:
			TeleportHandler.open(player, TeleportType.MINIGAMES);
			return true;
		case 1540:
		case 13069:
		case -7522:
			TeleportHandler.open(player, TeleportType.SKILLING);
			return true;
		case 1170:
		case 13053:
		case -7518:
			TeleportHandler.open(player, TeleportType.MONSTER_KILLING);
			return true;
		case 1164:
		case 13035:
		case -7514:
			TeleportHandler.open(player, TeleportType.PLAYER_KILLING);
			return true;
		case 1174:
		case 13061:
		case -7510:
			TeleportHandler.open(player, TeleportType.CITIES);
			return true;
		case -7497:
		case -7496:
			TeleportHandler.favorite(player);
			return true;
		}
		 */
	}
}