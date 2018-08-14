package io.battlerune.content.dailyachievement;

import java.util.HashMap;

/**
 * Handles the clicking on the achievement tab itemcontainer.
 * @author Adam - discord = Adam_#6723
 */
public class DailyAchievementButton {
	/**
	 * Holds all the Daily achievement buttons
	 */
	public static final HashMap<Integer, DailyAchievementList> DAILYACHIEVEMENT_BUTTONS = new HashMap<Integer, DailyAchievementList>();

	/**
	 * Holds all the achievement titles.
	 */
	public static final HashMap<Integer, DailyAchievementDifficulty> DAILYACHIEVEMENT_TITLES = new HashMap<Integer, DailyAchievementDifficulty>();

	static HashMap<Integer, DailyAchievementList> getAchievementButtons() {
		return DAILYACHIEVEMENT_BUTTONS;
	}

	static HashMap<Integer, DailyAchievementDifficulty> getAchievementTitles() {
		return DAILYACHIEVEMENT_TITLES;
	}
}
