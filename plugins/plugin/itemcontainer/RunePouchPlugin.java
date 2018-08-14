package plugin.itemcontainer;

import com.nardah.game.event.impl.ItemContainerContextMenuEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendInputAmount;

public class RunePouchPlugin extends PluginContext {

	@Override
	protected boolean firstClickItemContainer(Player player, ItemContainerContextMenuEvent event) {
		if (event.getInterfaceId() == 41710) {
			Item item = new Item(event.getRemoveId());
			player.runePouch.withdraw(item.getId(), 1);
			return true;
		}

		if (event.getInterfaceId() != 41711)
			return false;

		Item item = new Item(event.getRemoveId());

		if (player.inventory.contains(item.getId())) {
			item.setAmount(player.inventory.computeAmountForId(item.getId()));
		}

		player.runePouch.deposit(item, 1);
		return true;
	}

	@Override
	protected boolean secondClickItemContainer(Player player, ItemContainerContextMenuEvent event) {
		if (event.getInterfaceId() == 41710) {
			Item item = new Item(event.getRemoveId());
			player.runePouch.withdraw(item.getId(), 10);
			return true;
		}

		if (event.getInterfaceId() != 41711)
			return false;

		Item item = new Item(event.getRemoveId());

		if (player.inventory.contains(item.getId())) {
			item.setAmount(player.inventory.computeAmountForId(item.getId()));
		}

		player.runePouch.deposit(item, 10);
		return true;
	}

	@Override
	protected boolean thirdClickItemContainer(Player player, ItemContainerContextMenuEvent event) {
		if (event.getInterfaceId() == 41710) {
			Item item = new Item(event.getRemoveId());
			player.runePouch.withdraw(item.getId(), 100);
			return true;
		}

		if (event.getInterfaceId() != 41711)
			return false;

		Item item = new Item(event.getRemoveId());

		if (player.inventory.contains(item.getId())) {
			item.setAmount(player.inventory.computeAmountForId(item.getId()));
		}

		player.runePouch.deposit(item, 100);
		return true;
	}

	@Override
	protected boolean fourthClickItemContainer(Player player, ItemContainerContextMenuEvent event) {
		if (event.getInterfaceId() == 41710) {
			Item item = new Item(event.getRemoveId());
			if (player.runePouch.containsId(item.getId())) {
				item.setAmount(player.runePouch.getRuneAmount(item.getId()));
			}
			player.runePouch.withdraw(item.getId(), item.getAmount());
			return true;
		}

		if (event.getInterfaceId() != 41711)
			return false;

		Item item = new Item(event.getRemoveId());

		if (player.inventory.contains(item.getId())) {
			item.setAmount(player.inventory.computeAmountForId(item.getId()));
		}

		player.runePouch.deposit(item, item.getAmount());
		return true;
	}

	@Override
	protected boolean fifthClickItemContainer(Player player, ItemContainerContextMenuEvent event) {
		if (event.getInterfaceId() == 41710) {
			Item item = new Item(event.getRemoveId());
			player.send(
					new SendInputAmount(amount -> player.runePouch.withdraw(item.getId(), Integer.parseInt(amount))));
			return true;
		}

		if (event.getInterfaceId() != 41711)
			return false;

		Item item = new Item(event.getRemoveId());

		if (player.inventory.contains(item.getId())) {
			item.setAmount(player.inventory.computeAmountForId(item.getId()));
		}

		player.send(new SendInputAmount(amount -> player.runePouch.deposit(item, Integer.parseInt(amount))));
		return true;
	}

}
