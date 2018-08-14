package plugin.click.object;

import com.nardah.game.event.impl.ObjectClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.ObjectDefinition;

public class OpenBankObjectClickPlugin extends PluginContext {

	@Override
	protected boolean firstClickObject(Player player, ObjectClickEvent event) {
		final ObjectDefinition def = event.getObject().getDefinition();

		if (def == null || def.name == null) {
			return false;
		}

		final String name = def.name.toLowerCase();

		if (name.contains("bank booth") || name.contains("clan bank") || name.equals("open chest")
				|| name.equals("bank chest") || name.equals("grand exchange booth")) {
			player.bank.open();
			return true;
		}

		return false;
	}

	@Override
	protected boolean secondClickObject(Player player, ObjectClickEvent event) {
		final ObjectDefinition def = event.getObject().getDefinition();

		if (def == null || def.name == null) {
			return false;
		}

		final String name = def.name.toLowerCase();

		if (name.contains("bank booth") || name.contains("clan bank") || name.equals("grand exchange booth")) {
			player.bank.open();
			return true;
		}

		return false;
	}

}
