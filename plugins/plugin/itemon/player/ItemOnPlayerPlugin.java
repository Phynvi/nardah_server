package plugin.itemon.player;

import com.nardah.game.event.impl.ItemOnPlayerEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class ItemOnPlayerPlugin extends PluginContext {

	@Override
	protected boolean itemOnPlayer(Player player, ItemOnPlayerEvent event) {

		Player other = event.getOther();

		switch (event.getUsed().getId()) {
		/* Slayer Gem (Enchanted Gem) */
		case 4155:
			player.slayer.startDuoDialogue(player, other);
			return true;
		}

		return false;
	}

}
