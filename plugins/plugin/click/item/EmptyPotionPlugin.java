package plugin.click.item;

import com.nardah.content.consume.PotionData;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

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
