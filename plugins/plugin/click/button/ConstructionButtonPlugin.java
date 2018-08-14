package plugin.click.button;

import io.battlerune.content.skill.impl.construction.BuildableInterface;
import io.battlerune.content.skill.impl.construction.BuildableType;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;

public class ConstructionButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		if (button >= -17995 && button <= -17963) {
			BuildableInterface.click(player, button);
			return true;
		}

		switch (button) {
		case -18008:
			player.house.construct();
			return true;
		case -18030:
			BuildableInterface.open(player, BuildableType.MAIN_OBJECT);
			return true;
		case -18029:
			BuildableInterface.open(player, BuildableType.SKILL_OBJECT);
			return true;
		case -18028:
			BuildableInterface.open(player, BuildableType.MISCELLENOUS_OBJECT);
			return true;
		case -18027:
			BuildableInterface.open(player, BuildableType.NPC);
			return true;
		}

		return false;
	}
}
