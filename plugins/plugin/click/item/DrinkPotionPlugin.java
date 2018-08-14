package plugin.click.item;

import java.util.Optional;

import com.nardah.content.activity.Activity;
import com.nardah.content.consume.PotionData;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;

public class DrinkPotionPlugin extends PluginContext {

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		Optional<PotionData> potion = PotionData.forId(event.getItem().getId());

		if (!potion.isPresent() || player.isDead() || !player.potionDelay.elapsed(1250)) {
			return false;
		}

		if (Activity.evaluate(player, it -> !it.canDrinkPotions(player))) {
			return true;
		}

		if (!potion.get().canDrink(player)) {
			return false;
		}

		if (!player.interfaceManager.isClear()) {
			player.interfaceManager.close(false);
		}

		player.animate(new Animation(829, UpdatePriority.LOW));
		player.potionDelay.reset();

		Item replace = PotionData.getReplacementItem(event.getItem());

		if (replace.getId() == 229) {
			player.inventory.remove(event.getItem());
		} else {
			player.inventory.replace(event.getItem().getId(), replace.getId(), event.getSlot(), true);
		}
		potion.get().onEffect(player);
		return true;
	}

}
