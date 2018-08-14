package plugin.click.button;

import io.battlerune.content.Skillguides.AttackApp;
import io.battlerune.content.Skillguides.DefenceApp;
import io.battlerune.content.Skillguides.HerbloreApp;
import io.battlerune.content.Skillguides.MagicApp;
import io.battlerune.content.Skillguides.PrayerApp;
import io.battlerune.content.Skillguides.RangingApp;
import io.battlerune.content.Skillguides.RunecraftingApp;
import io.battlerune.content.Skillguides.StrengthApp;
import io.battlerune.content.skill.impl.firemaking.FiremakingData;
import io.battlerune.content.skill.impl.magic.teleport.TeleportType;
import io.battlerune.content.teleport.TeleportHandler;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.mob.player.Player;

public class SkillsButtonPlugin extends PluginContext {

	FiremakingData firemaking = null;

	/**
	 * @author adameternal123 / Arlo handles clicking on the skills button. making
	 *         it similar to how ruse had it handled.
	 */
	@Override
	protected boolean onClick(Player player, int button) {
		// COMBAT GUIDES
		if (button == 8654) {// ATTACK
			AttackApp.open(player);
		}
		if (button == 8657) {// STR
			StrengthApp.open(player);
		}
		if (button == 8660) {// DEF
			DefenceApp.open(player);
		}
		if (button == 8663) {// RANGE
			RangingApp.open(player);
		}
		if (button == 8669) {// MAGE
			MagicApp.open(player);
		}
		if (button == 8666) {// PRAYER
			PrayerApp.open(player);

		}
		// N/A
		if (button == 8655) {
			player.message("This skill does not have a guide �!");
		}
		// SKILLING GUIDES
//		if (button == 8658) {
//			TeleportHandler.open(player, TeleportType.SKILLING);
//		}

		return false;
	}
}
