package plugin.click.button;

import com.nardah.content.ItemsKeptOnDeath;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class EquipmentButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		if (button == 27653) {
			player.equipment.openInterface();
			return true;
		}
		if (button == 27654) {
			ItemsKeptOnDeath.open(player);
			return true;
		}
		return false;
	}
}