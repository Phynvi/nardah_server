package plugin;

import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.content.donators.DonatorBond;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Area;

/**
 * Handles the donator plugin.
 *
 * @author Daniel.
 * @author Adam_#6723
 */
public class DonatorPlugin extends PluginContext {

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		DonatorBond bond = DonatorBond.forId(event.getItem().getId());

		if (bond == null)
			return false;

		DialogueFactory factory = player.dialogueFactory;

		if (Area.inWilderness(player)) {
			factory.sendStatement("You can not be in the wilderness to redeem a bond!").execute();
			return true;
		}

		if (player.getCombat().inCombat()) {
			factory.sendStatement("You can not be in combat to redeem a bond!").execute();
			return true;
		}

		factory.sendStatement("Are you sure you want to redeem this <col=255>" + event.getItem().getName() + "</col>?",
				"There is no going back!");
		factory.sendOption("Yes, redeem <col=255>" + event.getItem().getName() + "</col>!",
				() -> redeem(player, event.getItem(), bond), "Nevrmind", factory::clear);
		factory.execute();
		return true;
	}

	private void redeem(Player player, Item item, DonatorBond bond) {
		if (!player.inventory.contains(item.getId(), 1))
			return;

		player.inventory.remove(item.getId(), 1);
		player.donation.redeem(bond);
	}
}
