package plugin.click.button;

import java.util.Arrays;
import java.util.Optional;

import io.battlerune.content.staff.PanelType;
import io.battlerune.content.staff.StaffAction;
import io.battlerune.content.staff.StaffPanel;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

public class StaffActionButtonPlugin extends PluginContext {

	private static final Logger logger = LogManager.getLogger(LoggerType.CONTENT);

	@Override
	protected boolean onClick(Player player, int button) {
		if (button >= -28805 && button <= -28700) {
			if (!player.interfaceManager.isInterfaceOpen(PanelType.INFORMATION_PANEL.getIdentification())
					&& !player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())
					&& !player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification())) {
				logger.error(String.format("Server defended against an interface hack on info panel by %s", player));
				return true;
			}
			StaffPanel.click(player, button);
			return true;
		}

		switch (button) {
		case -28829:
			if (player.interfaceManager.isInterfaceOpen(PanelType.INFORMATION_PANEL.getIdentification())) {
				return true;
			}

			if (!player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification()) && !player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())) {
				logger.error(String.format("Server defended against an interface hack on info panel by %s", player));
				return true;
			}
			StaffPanel.open(player, PanelType.INFORMATION_PANEL);
			return true;
		case -28828:
			if (player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification())) {
				return true;
			}

			if (!player.interfaceManager.isInterfaceOpen(PanelType.INFORMATION_PANEL.getIdentification()) && !player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())) {
				logger.error(String.format("Server defended against an interface hack on action panel by %s", player));
				return true;
			}
			StaffPanel.open(player, PanelType.ACTION_PANEL);
			return true;
		case -28827:
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

		final Optional<StaffAction> result = StaffAction.forAction(button);

		if (!result.isPresent()) {
			return false;
		}

		final StaffAction action = result.get();

		if (!player.interfaceManager.isInterfaceOpen(PanelType.ACTION_PANEL.getIdentification())) {
			logger.error(String.format("Server defended against an interface hack on action panel by %s action=%s", player, action.getName()));
			return true;
		}

		if (Arrays.stream(action.getRights()).noneMatch(it -> player.right.equals(it))) {
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

		action.handle(player, other);
		return true;
	}
}
