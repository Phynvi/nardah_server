package plugin.itemon.object;

import com.nardah.game.event.impl.ItemOnObjectEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;

public class UltimateIronmanPlugin extends PluginContext {

	@Override
	protected boolean itemOnObject(Player player, ItemOnObjectEvent event) {
		final Item used = event.getUsed();
		if (/*player.right.equals(PlayerRight.ULTIMATE_IRONMAN)
				&& */event.getObject().getDefinition().name.equals("Bank booth")) {
			if (!used.isTradeable()) {
				player.dialogueFactory.sendStatement("This item can not be noted!").execute();
				return true;
			}

			int free = player.inventory.getFreeSlots();
			int amount = player.inventory.computeAmountForId(used.getId());

			if (used.isNoted()) {
				if (free == 0) {
					player.dialogueFactory.sendStatement("You have no free inventory spaces to do this!").execute();
					return true;
				}

				if (amount > free)
					amount = free;

				player.inventory.remove(used.getId(), amount);
				player.inventory.add(new Item(used.getUnnotedId(), amount));
				player.dialogueFactory.sendStatement("You have un-noted " + amount + " " + used.getName() + ".")
						.execute();
				return true;
			}

			if (used.isNoteable()) {
				player.inventory.remove(used.getId(), amount);
				player.inventory.add(new Item(used.getNotedId(), amount));
				player.dialogueFactory.sendStatement("You have noted " + amount + " " + used.getName() + ".").execute();
				return true;
			}
		}

		return false;
	}

}
