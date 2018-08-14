package plugin.itemon.object;

import io.battlerune.game.event.impl.ItemOnObjectEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;

public class ConstructionToolKitPlugin extends PluginContext {

	@Override
	protected boolean itemOnObject(Player player, ItemOnObjectEvent event) {
		return event.getUsed().getId() == 1 && player.house.toolkit(event.getObject());
	}

}
