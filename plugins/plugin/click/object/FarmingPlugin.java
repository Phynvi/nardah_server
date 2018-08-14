package plugin.click.object;

import io.battlerune.game.event.impl.ObjectClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;

/**
 * @author Ethan Kyle Millard <skype:pumpklins>
 * @since Sun, May 20, 2018 @ 1:48 PM
 * @edited and fixed by - Adam_#6723
 */
public class FarmingPlugin extends PluginContext {


	@Override
	protected boolean firstClickObject(Player player, ObjectClickEvent event) {
		return player.getFarming().click(player, event.getObject().getX(), event.getObject().getY(), 1);
	}
}
