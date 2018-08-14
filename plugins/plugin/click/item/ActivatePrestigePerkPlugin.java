package plugin.click.item;

import java.util.HashSet;

import com.nardah.content.prestige.PrestigePerk;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;

public class ActivatePrestigePerkPlugin extends PluginContext {

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		if (!PrestigePerk.forItem(event.getItem().getId()).isPresent()) {
			return false;
		}

		final PrestigePerk perk = PrestigePerk.forItem(event.getItem().getId()).get();

		if (player.prestige.activePerks == null) {
			player.prestige.activePerks = new HashSet<>();
		}

		if (player.prestige.activePerks.contains(perk)) {
			player.send(new SendMessage("The Perk: " + perk.name + " perk is already active on your account!",
					MessageColor.DARK_BLUE));
			return true;
		}

		player.inventory.remove(event.getItem());
		player.prestige.activePerks.add(perk);
		player.send(
				new SendMessage("You have successfully activated the " + perk.name + " perk.", MessageColor.DARK_BLUE));
		return true;
	}

}
