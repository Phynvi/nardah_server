package plugin;

import com.nardah.game.event.impl.DropItemEvent;
import com.nardah.game.event.impl.MovementEvent;
import com.nardah.game.event.impl.PickupItemEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.data.ValueIcon;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;

public class ValueIconPlugin extends PluginContext {

	@Override
	protected boolean onMovement(Player player, MovementEvent event) {
		if (player.valueIcon != -1 && !Area.inWilderness(player)) {
			player.valueIcon = -1;
			player.updateFlags.add(UpdateFlag.APPEARANCE);
			return false; // if this is true any other plugins that use this method wont work
		}

		if (player.valueIcon == -1 && Area.inWilderness(player)) {
			player.valueIcon = player.playerAssistant.getValueIcon(player).getCode();
			player.updateFlags.add(UpdateFlag.APPEARANCE);
			return false; // if this is true any other plugins that use this method wont work
		}
		return false;
	}

	@Override
	protected boolean onPickupItem(Player player, PickupItemEvent event) {
		final Item item = event.getItem();

		if (item.getValue() * item.getAmount() <= 10_000) {
			return false; // if this is true any other plugins that use this method wont work
		}

		final Position position = event.getPosition();

		if (Area.inWilderness(position)) {
			ValueIcon icon = player.playerAssistant.getValueIcon(player);

			final int currentIcon = player.valueIcon;

			if (icon.getCode() == currentIcon) {
				return false; // if this is true any other plugins that use this method wont work
			}

			player.valueIcon = icon.getCode();
			player.updateFlags.add(UpdateFlag.APPEARANCE);
		}

		return false;
	}

	@Override
	protected boolean onDropItem(Player player, DropItemEvent event) {
		final Item item = event.getItem();

		if (item.getValue() * item.getAmount() <= 10_000) {
			return false; // if this is true any other plugins that use this method wont work
		}

		final Position position = event.getPosition();

		if (Area.inWilderness(position)) {
			ValueIcon icon = player.playerAssistant.getValueIcon(player);

			final int currentIcon = player.valueIcon;

			if (icon.getCode() == currentIcon) {
				return false; // if this is true any other plugins that use this method wont work
			}

			player.valueIcon = icon.getCode();
			player.updateFlags.add(UpdateFlag.APPEARANCE);
		}
		return false;
	}

}
