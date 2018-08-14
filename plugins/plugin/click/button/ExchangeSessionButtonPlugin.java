package plugin.click.button;

import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.exchange.ExchangeSession;

public class ExchangeSessionButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		ExchangeSession session = ExchangeSession.getSession(player).orElse(null);
		return session != null && session.onButtonClick(player, button);
	}
}
