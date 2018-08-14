package plugin.click.button;

import static com.nardah.content.dailyachievement.DailyAchievementButton.DAILYACHIEVEMENT_BUTTONS;
import static com.nardah.content.dailyachievement.DailyAchievementButton.DAILYACHIEVEMENT_TITLES;

import com.nardah.content.dailyachievement.DailyAchievementDifficulty;
import com.nardah.content.dailyachievement.DailyAchievementHandler;
import com.nardah.content.dailyachievement.DailyAchievementList;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;
import com.nardah.util.Utility;

public class DailyAcheivementButtonPlugin extends PluginContext {

	// basically copied the current achievement system, and suited it to a daily
	// achievement system.

	@Override
	protected boolean onClick(Player player, int button) {
		if (DAILYACHIEVEMENT_BUTTONS.containsKey(button)) {
			DailyAchievementList achievement = DAILYACHIEVEMENT_BUTTONS.get(button);
			boolean completed = DailyAchievementHandler.completed(player, achievement);
			int completion = player.playerAchievements1.computeIfAbsent(achievement.getKey(), a -> 0);
			int progress = (int) (completion * 100 / (double) achievement.getAmount());
			String remaining = " (" + Utility.formatDigits((achievement.getAmount() - completion)) + " remaining).";
			player.send(new SendMessage("You have completed " + (progress > 100 ? 100 : progress)
					+ "% of this achievement" + (completed ? "." : remaining), MessageColor.DARK_BLUE));
			return true;
		}

		if (DAILYACHIEVEMENT_TITLES.containsKey(button)) {
			DailyAchievementDifficulty difficulty = DAILYACHIEVEMENT_TITLES.get(button);
			int completed = DailyAchievementHandler.getDifficultyCompletion(player, difficulty);
			int total = DailyAchievementHandler.getDifficultyAchievement(difficulty);
			player.send(new SendMessage("You have completed a total of " + completed + "/" + total + " "
					+ difficulty.name().toLowerCase() + " achievements.", MessageColor.DARK_BLUE));
			return true;
		}
		return false;
	}
}
