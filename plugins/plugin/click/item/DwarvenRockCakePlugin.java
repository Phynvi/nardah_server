package plugin.click.item;

import java.util.concurrent.TimeUnit;

import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Area;

public class DwarvenRockCakePlugin extends PluginContext {

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		if (event.getItem().getId() == 7509) {
			if (!player.itemDelay.elapsed(599, TimeUnit.MILLISECONDS))
				return true;

			if (player.getCombat().inCombat()) {
				player.message("You can not eat this while in combat!");
				return true;
			}

			if (Area.inWilderness(player)) {
				player.message("You better not eat this while in the wilderness!");
				return true;
			}

			int health = player.getCurrentHealth();
			int damage = health - 1;

			if (damage <= 0) {
				player.message("You better not eat that!");
				return true;
			}

			player.speak("Ouch!");
			player.damage(new Hit(player.getCurrentHealth() - 1));
			player.itemDelay.reset();
			return true;
		}
		return false;
	}
}
