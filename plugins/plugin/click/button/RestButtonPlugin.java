package plugin.click.button;

import com.nardah.game.Animation;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Area;
import com.nardah.net.packet.out.SendMessage;

public class RestButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		if (button == 1059) {
			String allowed = "";
			if (Area.inWilderness(player))
				allowed = "You can not rest whilst in the wilderness!";
			if (player.getCombat().inCombat())
				allowed = "You can not rest while in combat.";
			if (player.playerAssistant.busy())
				allowed = "You can't do this rest now!";
			if (player.locking.locked())
				allowed = "You can't do this rest now!";
			if (!allowed.equals("")) {
				player.send(new SendMessage(allowed));
				return true;
			}
			int animation = player.right.getRestAnimation();
			player.resting = true;
			player.animate(new Animation(animation));
			player.send(new SendMessage("You are now resting."));
			return true;
		}
		return false;
	}
}