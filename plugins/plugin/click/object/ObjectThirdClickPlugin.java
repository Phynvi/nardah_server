package plugin.click.object;

import io.battlerune.Config;
import io.battlerune.content.skill.impl.magic.Spellbook;
import io.battlerune.game.Animation;
import io.battlerune.game.event.impl.ObjectClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.combat.magic.Autocast;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendMessage;

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
