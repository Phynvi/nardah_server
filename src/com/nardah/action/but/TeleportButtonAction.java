package com.nardah.action.but;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.ButtonAction;
import com.nardah.content.skill.impl.magic.teleport.TeleportType;
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
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.click(player, button);
				return true;
			}
		};
		for (int button = -7484; button <= -7440; button++) { action.register(button); }

		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.open(player, TeleportType.FAVORITES);
				return true;
			}
		};
		action.register(-7530);
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.open(player, TeleportType.MINIGAMES);
				return true;
			}
		};
		action.register(-7530).register(13079).register(-7526);
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.open(player, TeleportType.SKILLING);
				return true;
			}
		};
		action.register(-7522).register(13069).register(1540);
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.open(player, TeleportType.MONSTER_KILLING);
				return true;
			}
		};
		action.register(-7518).register(13053).register(1170);
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.open(player, TeleportType.PLAYER_KILLING);
				return true;
			}
		};
		action.register(-7514).register(13035).register(1164);
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.open(player, TeleportType.CITIES);
				return true;
			}
		};
		action.register(-7510).register(13061).register(1174);
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				TeleportHandler.favorite(player);
				return true;
			}
		};
		action.register(-7497).register(-7496);

	}
}