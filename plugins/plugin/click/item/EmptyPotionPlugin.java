package plugin.click.item;

import io.battlerune.content.consume.PotionData;
import io.battlerune.game.event.impl.ItemClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendMessage;

public class EmptyPotionPlugin extends PluginContext {

	@Override
	protected boolean thirdClickItem(Player player, ItemClickEvent event) {
		if (PotionData.forId(event.getItem().getId()).isPresent()) {
			player.inventory.replace(event.getItem().getId(), 229, true);
			player.send(
					new SendMessage("You have poured out the remaining dose(s) of " + event.getItem().getName() + "."));
			return true;
		}
		return false;
	}

}
