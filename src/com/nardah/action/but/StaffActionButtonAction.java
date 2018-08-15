package com.nardah.action.but;

import java.util.Arrays;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.ButtonAction;
import com.nardah.content.staff.PanelType;
import com.nardah.content.staff.StaffAction;
import com.nardah.content.staff.StaffPanel;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

public class StaffActionButtonAction extends ActionInitializer {

	private static final Logger logger = LogManager.getLogger(LoggerType.CONTENT);
	
	@Override
	public void init() {
		ButtonAction action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				if (player.interfaceManager.isInterfaceOpen(PanelType.INFORMATION_PANEL.getIdentification())) {
					return true;
				}
				
				if (!player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification()) && !player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())) {
					logger.error(String.format("Server defended against an interface hack on info panel by %s", player));
					return true;
				}
				StaffPanel.open(player, PanelType.INFORMATION_PANEL);
				return true;
			}
		};
		action.register(-28829);
		
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				if (player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification())) {
					return true;
				}
				
				if (!player.interfaceManager.isInterfaceOpen(PanelType.INFORMATION_PANEL.getIdentification()) && !player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())) {
					logger.error(String.format("Server defended against an interface hack on action panel by %s", player));
					return true;
				}
				StaffPanel.open(player, PanelType.ACTION_PANEL);
				return true;
			}
		};
		action.register(-28828);
		
		action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				if (player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())) {
					return true;
				}
				
				if (!player.interfaceManager.isInterfaceOpen(PanelType.INFORMATION_PANEL.getIdentification()) && !player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification())) {
					logger.error(String.format("Server defended against an interface hack on developer panel by %s", player));
					return true;
				}
				StaffPanel.open(player, PanelType.DEVELOPER_PANEL);
				return true;
			}
		};
		action.register(-28827);
		
		for(StaffAction a : StaffAction.values()) {
			action = new ButtonAction() {
				@Override
				public boolean click(Player player, int button) {
					if (!player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification())) {
						logger.error(String.format("Server defended against an interface hack on action panel by %s action=%s", player, a.getName()));
						return true;
					}
					
					if (Arrays.stream(a.getRights()).noneMatch(it -> player.right.equals(it))) {
						player.send(new SendMessage("You do not have sufficient permission to use this.", MessageColor.DARK_BLUE));
						return true;
					}
					
					Player other = player.attributes.get("PLAYER_PANEL_KEY", Player.class);
					
					if (other == null) {
						player.send(new SendMessage("The player you have selected is currently invalid.", MessageColor.DARK_BLUE));
						return true;
					}
					
					if (player == other && !PlayerRight.isDeveloper(player)) {
						player.send(new SendMessage("You can't manage yourself!", MessageColor.DARK_BLUE));
						return true;
					}
					
					a.handle(player, other);
					return true;
				}
			};
			action.register(a.getButton());
		}
		
		for(int button = -28805; button <= 28700; button++) {
			action = new ButtonAction() {
				@Override
				public boolean click(Player player, int button) {
					if (!player.interfaceManager.isInterfaceOpen(PanelType.INFORMATION_PANEL.getIdentification())
							&& !player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())
							&& !player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification())) {
						logger.error(String.format("Server defended against an interface hack on info panel by %s", player));
						return true;
					}
					StaffPanel.click(player, button);
					return true;
				}
			};
			action.register(button);
		}
	}
}
