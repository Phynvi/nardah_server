package plugin.click.button;

import io.battlerune.content.activity.ActivityType;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;

public class GameRecordButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		if (button == 32307) {
			player.gameRecord.global = true;
			player.gameRecord.display(ActivityType.getFirst());
			return true;
		}
		if (button == 32309) {
			player.gameRecord.global = false;
			player.gameRecord.display(ActivityType.getFirst());
			return true;
		}
		int base_button = 32352;
		int index = (button - base_button) / 2;
		ActivityType activity = ActivityType.getOrdinal(index);
		if (activity == null)
			return false;
		player.gameRecord.display(activity);
		return true;
	}
}
