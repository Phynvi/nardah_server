package plugin.click.item;

import java.util.Optional;

import io.battlerune.content.activity.Activity;
import io.battlerune.content.consume.FoodData;
import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.event.impl.ItemClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendMessage;

public class EatFoodPlugin extends PluginContext {

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		final Optional<FoodData> foodResult = FoodData.forId(event.getItem().getId());

		if (!foodResult.isPresent()) {
			return false;
		}

		final FoodData foodType = foodResult.get();

		if (Activity.evaluate(player, it -> !it.canEat(player, foodType))) {
			return true;
		}

		if (!player.interfaceManager.isClear()) {
			player.interfaceManager.close(false);
		}

		eat(player, event.getItem(), event.getSlot(), foodType);
		return true;
	}

	public static void eat(Player player, Item item, int slot, FoodData food) {
		if (food == FoodData.COOKED_KARAMBWAN && player.karambwanDelay.elapsed(800) || player.foodDelay.elapsed(1300)) {
			if (food == FoodData.COOKED_KARAMBWAN) {
				player.karambwanDelay.reset();
			}

			int heal = food.getHeal();
			int maxHealth = player.getCurrentHealth() > player.getMaximumHealth() ? player.getCurrentHealth()
					: player.getMaximumHealth();
			if (food == FoodData.ANGLERFISH && maxHealth < FoodData.anglerfishHeal(player)) {
				maxHealth = FoodData.anglerfishHeal(player);
			}
			player.animate(new Animation(829, UpdatePriority.LOW));
			player.getCombat().reset();
			player.getCombat().cooldown(2);
			player.foodDelay.reset();
			player.inventory.remove(item, slot);
			player.skills.get(Skill.HITPOINTS).modifyLevel(level -> level + heal, 0, maxHealth);
			player.skills.refresh(Skill.HITPOINTS);
			player.send(new SendMessage("You eat the " + food.getName() + "."));
		}
	}

}
