package com.nardah.content.dailyachievement;

import com.nardah.content.writer.InterfaceWriter;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.*;
import io.battlerune.net.packet.out.*;
import com.nardah.util.Utility;

import java.util.Arrays;

/**
 * Handles the achievement itemcontainer.
 * @author Adam - discord = Adam_#6723
 */
public class DailyAchievementInterface extends InterfaceWriter {

	private final static int BASE_BUTTON = -28205;
	private final String[] text;

	public DailyAchievementInterface(Player player) {
		super(player);
		int shift = 0;
		int total = DailyAchievementList.values().length;
		text = new String[total + DailyAchievementDifficulty.values().length + 3];
		Arrays.fill(text, "");

		DailyAchievementDifficulty last = null;
		for(DailyAchievementList achievement : DailyAchievementList.values()) {
			if(last != achievement.getDifficulty()) {
				last = achievement.getDifficulty();
				if(shift != 0) {
					text[shift++] = "";
				}

				int completion = DailyAchievementHandler.getDifficultyCompletion(player, achievement.getDifficulty());
				int progress = (int) (completion * 100 / (double) DailyAchievementHandler.getDifficultyAchievement(achievement.getDifficulty()));

				player.send(new SendFont(startingIndex() + shift, 2));
				player.send(new SendTooltip("", startingIndex() + shift));
				text[shift++] = "<col=CF851B>" + Utility.formatEnum(last.name() + ": " + progress + "%");

				int button = (shift - 1) + BASE_BUTTON;

				if(!DailyAchievementButton.getAchievementTitles().containsKey(button)) {
					DailyAchievementButton.getAchievementTitles().put(button, achievement.getDifficulty());
				}
			}

			int completed = player.playerAchievements1.computeIfAbsent(achievement.getKey(), a -> 0);
			if(completed > achievement.getAmount()) {
				completed = achievement.getAmount();
			}

			int color = completed == achievement.getAmount() ? 0x00FF00 : completed > 0 ? 0xFFFF00 : 0xFF0000;
			player.send(new SendColor(startingIndex() + shift, color));
			player.send(new SendTooltip("View achievement " + achievement.getTask() + "", startingIndex() + shift));
			if(shift < text.length)
				text[shift++] = "" + achievement.getTask();
			// if the shift size is < text.lengt
			int button = (shift - 1) + BASE_BUTTON;

			if(!DailyAchievementButton.getAchievementButtons().containsKey(button)) {
				DailyAchievementButton.getAchievementButtons().put(button, achievement);
			}
		}

		int progress = (int) (DailyAchievementHandler.getTotalCompleted(player) * 100 / (double) DailyAchievementList.getTotal());

		player.send(new SendScrollbar(36023, 1500));
		player.send(new SendString("Completed: " + DailyAchievementHandler.getTotalCompleted(player) + "/" + total + " (" + progress + "%)", 36037));
	}

	@Override
	protected int startingIndex() {
		return 36201;
	}

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int[][] color() {
		return null;
	}

	@Override
	protected int[][] font() {
		return null;
	}

}
