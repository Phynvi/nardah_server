package plugin.click.object;

import com.nardah.game.event.impl.ObjectClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

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
