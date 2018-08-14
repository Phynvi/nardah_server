package plugin.click.button;

import com.nardah.content.Skillguides.AttackApp;
import com.nardah.content.Skillguides.DefenceApp;
import com.nardah.content.Skillguides.MagicApp;
import com.nardah.content.Skillguides.PrayerApp;
import com.nardah.content.Skillguides.RangingApp;
import com.nardah.content.Skillguides.StrengthApp;
import com.nardah.content.skill.impl.firemaking.FiremakingData;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

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
