package plugin.click.button;

import io.battlerune.content.skill.impl.magic.teleport.TeleportType;
import io.battlerune.content.teleport.TeleportHandler;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.mob.player.Player;

public class TeleportButtonPlugin extends PluginContext {

	/**
	 * @Adam_#6723
	 */
	@Override
	protected boolean onClick(Player player, int button) {
		if (button >= -7484 && button <= -7440) {
			TeleportHandler.click(player, button);
			return true;
		}
		switch (button) {
		case 850:
			// ADAM I SWEAR TO FUCKING CHRIST IF YOU REMOVE THIS LINE ONE MORE GODDAMN TIME
			// I'M<<<<<<<<<<<<calm down| DRIVING A TRUCK DOWNTOWN SWEDEN AGAIN>>>>>>>>>>>>>
			// I'M FUCKING DONE WITH YOUR SHIT
			TeleportHandler.open(player);
			return true;
		case -7501:
			TeleportHandler.teleport(player);
			return true;
		case -7530:
			TeleportHandler.open(player, TeleportType.FAVORITES);
			return true;
		case 1541:
		case 13079:
		case -7526:
			TeleportHandler.open(player, TeleportType.MINIGAMES);
			return true;
		case 1540:
		case 13069:
		case -7522:
			TeleportHandler.open(player, TeleportType.SKILLING);
			return true;
		case 1170:
		case 13053:
		case -7518:
			TeleportHandler.open(player, TeleportType.MONSTER_KILLING);
			return true;
		case 1164:
		case 13035:
		case -7514:
			TeleportHandler.open(player, TeleportType.PLAYER_KILLING);
			return true;
		case 1174:
		case 13061:
		case -7510:
			TeleportHandler.open(player, TeleportType.CITIES);
			return true;
		case -7497:
		case -7496:
			TeleportHandler.favorite(player);
			return true;
		}
		return false;
	}
}