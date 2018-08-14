package plugin.click.button;

import java.util.Optional;

import io.battlerune.content.staff.DeveloperAction;
import io.battlerune.content.staff.PanelType;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

public class DeveloperActionButtonPlugin extends PluginContext {

	private static final Logger logger = LogManager.getLogger(LoggerType.CONTENT);

	@Override
	protected boolean onClick(Player player, int button) {
		final Optional<DeveloperAction> result = DeveloperAction.forAction(button);

		if (!result.isPresent()) {
			return false;
		}

		final DeveloperAction action = result.get();

		if (!player.interfaceManager.isInterfaceOpen(PanelType.DEVELOPER_PANEL.getIdentification())) {
			logger.error(String.format("Server defended against an interface hack on developer panel by %s action=%s", player, action.getName()));
			return true;
		}

		final Player other = player.attributes.get("PLAYER_PANEL_KEY", Player.class);

		if (other == null) {
			player.send(new SendMessage("The player you have selected is currently invalid.", MessageColor.DARK_BLUE));
			return true;
		}

		if (!PlayerRight.isDeveloper(player) && player == other) {
			player.send(new SendMessage("You can't manage yourself!", MessageColor.DARK_BLUE));
			return true;
		}

		action.handle(player);
		return true;
	}

}
