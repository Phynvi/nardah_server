package plugin.click.button;

import com.nardah.content.Obelisks;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class ObeliskButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		int obelisk = player.attributes.get("OBELISK", Integer.class);
		if (obelisk != -1) {
			switch (button) {
			case -14524:
				Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.LEVEL_44);
				return true;
			case -14523:
				Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.LEVEL_27);
				return true;
			case -14522:
				Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.LEVEL_35);
				return true;
			case -14521:
				Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.LEVEL_13);
				return true;
			case -14520:
				Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.LEVEL_19);
				return true;
			case -14519:
				Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.LEVEL_50);
				return true;
			}
		}
		return false;
	}
}
