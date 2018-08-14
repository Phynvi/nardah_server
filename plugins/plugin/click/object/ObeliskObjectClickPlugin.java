package plugin.click.object;

import com.nardah.content.Obelisks;
import com.nardah.game.event.impl.ObjectClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class ObeliskObjectClickPlugin extends PluginContext {

	@Override
	protected boolean firstClickObject(Player player, ObjectClickEvent event) {
		return Obelisks.get().activate(player, event.getObject().getId());
	}

}
