package plugin.itemon.object;

import com.nardah.game.event.impl.ItemOnObjectEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class ConstructionToolKitPlugin extends PluginContext {

	@Override
	protected boolean itemOnObject(Player player, ItemOnObjectEvent event) {
		return event.getUsed().getId() == 1 && player.house.toolkit(event.getObject());
	}

}
