package plugin.click.button;

import com.nardah.content.skill.impl.construction.BuildableInterface;
import com.nardah.content.skill.impl.construction.BuildableType;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

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
