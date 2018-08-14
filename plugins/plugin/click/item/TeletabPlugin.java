package plugin.click.item;

import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.content.teleport.TeleportTablet;
import io.battlerune.game.event.impl.ItemClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

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
