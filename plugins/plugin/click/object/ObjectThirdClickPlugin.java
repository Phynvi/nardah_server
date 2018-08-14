package plugin.click.object;

import com.nardah.Config;
import com.nardah.content.skill.impl.magic.Spellbook;
import com.nardah.game.Animation;
import com.nardah.game.event.impl.ObjectClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.combat.magic.Autocast;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

public class ObjectThirdClickPlugin extends PluginContext {

	@Override
	protected boolean thirdClickObject(Player player, ObjectClickEvent event) {
		final int id = event.getObject().getId();

		switch (id) {
		case 29150:
			Autocast.reset(player);
			player.animate(new Animation(645));
			player.spellbook = Spellbook.LUNAR;
			player.interfaceManager.setSidebar(Config.MAGIC_TAB, player.spellbook.getInterfaceId());
			player.send(
					new SendMessage("You are now using the " + player.spellbook.name().toLowerCase() + " spellbook."));
			break;

		}

		return false;
	}

}
