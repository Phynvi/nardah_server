package plugin.click.button;

import com.nardah.content.store.StoreType;
import com.nardah.content.store.impl.PersonalStore;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class PersonalStoreButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		switch (button) {
		case -27321:
			PersonalStore.openPanel(player);
			return true;
		case -27318:
			PersonalStore.claimCoins(player);
			return true;
		case -27315:
			player.interfaceManager.open(38300);
			return true;
		case -27312:
			PersonalStore.myShop(player);
			return true;

		case -27226:
			PersonalStore.openMenu(player);
			return true;
		}

		boolean featured = button >= -12600 && button <= -12528;
		PersonalStore shop = featured ? PersonalStore.FEATURED_SHOPS.get(button) : player.viewing_shops.get(button);
		if (shop == null)
			return false;
		if (!PersonalStore.STORES.containsKey(shop.name) || !shop.type().equals(StoreType.PERSONAL))
			return false;
		shop.open(player);
		return true;
	}
}
