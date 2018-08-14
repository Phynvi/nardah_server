package plugin.itemon;

import com.nardah.game.event.impl.ItemOnNpcEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;

/**
 * Handles the rotten tomatoe plugin.
 *
 * @author Daniel.
 */
public class RottenTomatoePlugin extends PluginContext {

	@Override
	protected boolean itemOnNpc(Player player, ItemOnNpcEvent event) {
		if (!PlayerRight.isDonator(player) || !PlayerRight.isDeveloper(player) || !PlayerRight.isManagement(player))
			return false;

		Item item = event.getUsed();

		if (item.getId() != 5733)
			return false;

		Mob mob = event.getMob();

		player.dialogueFactory.sendStatement("<col=255>" + mob.getName(), "spawn=" + mob.spawnPosition + "");
		player.dialogueFactory.execute();
		return true;
	}
}
