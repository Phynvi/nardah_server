package plugin.click.button;

import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.exchange.ExchangeSession;

public class ExchangeSessionButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		ExchangeSession session = ExchangeSession.getSession(player).orElse(null);
		return session != null && session.onButtonClick(player, button);
	}
}
