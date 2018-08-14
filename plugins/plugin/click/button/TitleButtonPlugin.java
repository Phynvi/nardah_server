package plugin.click.button;

import com.nardah.content.tittle.TitleManager;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class TitleButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		if (button >= -26485 && button <= -26420) {
			TitleManager.click(player, button);
			return true;
		}
		switch (button) {
		case -26525:
			TitleManager.redeem(player);
			break;
		case -26528:
			TitleManager.reset(player);
			break;
		}
		return false;
	}
}
