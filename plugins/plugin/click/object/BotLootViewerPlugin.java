package plugin.click.object;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.battlerune.content.bot.BotUtility;
import io.battlerune.game.event.impl.ObjectClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendItemOnInterfaceSlot;
import io.battlerune.net.packet.out.SendString;
import io.battlerune.util.MutableNumber;
import io.battlerune.util.Utility;

public class BotLootViewerPlugin extends PluginContext {

	@Override
	protected boolean firstClickObject(Player player, ObjectClickEvent event) {
		if (event.getObject().getId() != 24099)
			return false;

		long value = 0;

		List<Item> items = new LinkedList<>();

		for (Map.Entry<Integer, MutableNumber> entry : BotUtility.BOOT_LOOT.entrySet()) {
			int id = entry.getKey();
			int amount = entry.getValue().get();

			Item item = new Item(id, amount);
			items.add(item);
			value += item.getValue() * item.getAmount();
		}

		items.sort((first, second) -> second.getValue() - first.getValue());

		int index = 0;
		for (Item item : items) {
			player.send(new SendItemOnInterfaceSlot(37560, item, index++));
		}

		player.send(new SendString("Total value: " + Utility.formatPrice(value), 37553));
		player.send(new SendString("Total items: " + Utility.formatDigits(index), 37554));
		player.interfaceManager.open(37550);
		return true;
	}

}
