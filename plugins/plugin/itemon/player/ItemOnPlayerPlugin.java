package plugin.itemon.player;

import io.battlerune.game.event.impl.ItemOnPlayerEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;

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
