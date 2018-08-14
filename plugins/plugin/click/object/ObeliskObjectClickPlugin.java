package plugin.click.object;

import io.battlerune.content.Obelisks;
import io.battlerune.game.event.impl.ObjectClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;

public class ObeliskObjectClickPlugin extends PluginContext {

	@Override
	protected boolean firstClickObject(Player player, ObjectClickEvent event) {
		return Obelisks.get().activate(player, event.getObject().getId());
	}

}
