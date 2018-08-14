package plugin.click.button;

import static io.battlerune.content.achievement.AchievementButton.ACHIEVEMENT_BUTTONS;
import static io.battlerune.content.achievement.AchievementButton.ACHIEVEMENT_TITLES;

import io.battlerune.content.achievement.AchievementDifficulty;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.achievement.AchievementList;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;
import io.battlerune.util.Utility;

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
