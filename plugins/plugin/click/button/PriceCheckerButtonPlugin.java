package plugin.click.button;

import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.containers.pricechecker.PriceType;
import io.battlerune.net.packet.out.SendMessage;

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
