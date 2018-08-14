package plugin.click.button;

import static io.battlerune.content.dailyachievement.DailyAchievementButton.DAILYACHIEVEMENT_BUTTONS;
import static io.battlerune.content.dailyachievement.DailyAchievementButton.DAILYACHIEVEMENT_TITLES;

import io.battlerune.content.dailyachievement.DailyAchievementDifficulty;
import io.battlerune.content.dailyachievement.DailyAchievementHandler;
import io.battlerune.content.dailyachievement.DailyAchievementList;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;
import io.battlerune.util.Utility;

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
