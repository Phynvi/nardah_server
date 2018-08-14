package plugin.click.button;

import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.containers.pricechecker.PriceType;
import com.nardah.net.packet.out.SendMessage;

public class PriceCheckerButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		switch (button) {
		case 27651:
			player.priceChecker.open();
			return true;
		case -17031:
			player.priceChecker.depositAll();
			return true;
		case -16958:
			player.priceChecker.withdrawAll();
			return true;
		case -17034:
			player.priceChecker.close();
			return true;
		case -16952:
			player.priceChecker.setValue(PriceType.VALUE);
			return true;
		case -16951:
			player.priceChecker.setValue(PriceType.HIGH_ALCH_VALUE);
			return true;
		case -17028:
			player.send(new SendMessage("Price checker search function is not yet available."));
			return true;
		}
		return false;
	}
}
