package plugin.click.item;

import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.content.teleport.TeleportTablet;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

public class TeletabPlugin extends PluginContext {

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		if (!TeleportTablet.forId(event.getItem().getId()).isPresent()) {
			return false;
		}

		final TeleportTablet tablet = TeleportTablet.forId(event.getItem().getId()).get();

		if (player.house.isInside()) {
			player.send(new SendMessage("Please leave the house before teleporting."));
			return true;
		}

		player.inventory.remove(new Item(event.getItem().getId(), 1));
		player.send(
				new SendMessage("You have broken the " + Utility.formatEnum(tablet.name()) + " and were teleported."));
		Teleportation.teleport(player, tablet.getPosition(), Teleportation.TeleportationData.TABLET, () -> {
		});
		return true;
	}

}
