package plugin.click.button;

import static com.nardah.content.achievement.AchievementButton.ACHIEVEMENT_BUTTONS;
import static com.nardah.content.achievement.AchievementButton.ACHIEVEMENT_TITLES;

import com.nardah.content.achievement.AchievementDifficulty;
import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementList;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;
import com.nardah.util.Utility;

public class AcheivementButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		if (ACHIEVEMENT_BUTTONS.containsKey(button)) {
			AchievementList achievement = ACHIEVEMENT_BUTTONS.get(button);
			boolean completed = AchievementHandler.completed(player, achievement);
			int completion = player.playerAchievements.computeIfAbsent(achievement.getKey(), a -> 0);
			int progress = (int) (completion * 100 / (double) achievement.getAmount());
			String remaining = " (" + Utility.formatDigits((achievement.getAmount() - completion)) + " remaining).";
			player.send(new SendMessage("You have completed " + (progress > 100 ? 100 : progress)
					+ "% of this achievement" + (completed ? "." : remaining), MessageColor.DARK_BLUE));
			return true;
		}

		if (ACHIEVEMENT_TITLES.containsKey(button)) {
			AchievementDifficulty difficulty = ACHIEVEMENT_TITLES.get(button);
			int completed = AchievementHandler.getDifficultyCompletion(player, difficulty);
			int total = AchievementHandler.getDifficultyAchievement(difficulty);
			player.send(new SendMessage("You have completed a total of " + completed + "/" + total + " "
					+ difficulty.name().toLowerCase() + " achievements.", MessageColor.DARK_BLUE));
			return true;
		}
		return false;
	}
}
