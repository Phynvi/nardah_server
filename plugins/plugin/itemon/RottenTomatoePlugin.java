package plugin.itemon;

import io.battlerune.game.event.impl.ItemOnNpcEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.items.Item;

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

		Npc npc = event.getNpc();

		player.dialogueFactory.sendStatement("<col=255>" + npc.getName(), "spawn=" + npc.spawnPosition + "");
		player.dialogueFactory.execute();
		return true;
	}
}
