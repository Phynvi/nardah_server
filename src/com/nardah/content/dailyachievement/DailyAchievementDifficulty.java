package com.nardah.content.dailyachievement;

/**
 * The types of achievement difficulties. where is the main file for the
 * dailyachievements
 * @author Adam - discord = Adam_#6723
 */
public enum DailyAchievementDifficulty {
	/**
	 * The easy achievement difficulty.
	 */
	EASY(50000, 0x1C889E),

	/**
	 * The medium achievement difficulty.
	 */
	MEDIUM(125000, 0xD9750B),

	/**
	 * The hard achievement difficulty.
	 */
	HARD(300000, 0xC41414),

	/**
	 * The elite achievement difficulty.
	 */
	ELITE(650000, 0xC52BE0);

	/**
	 * The reward point for completing the achievement
	 */
	private final int reward;

	/**
	 * The color of the completion banner
	 */
	private final int color;

	/**
	 * Achievement difficulty
	 */
	DailyAchievementDifficulty(int reward, int color) {
		this.reward = reward;
		this.color = color;
	}

	/**
	 * Gets the reward point
	 */
	public int getReward() {
		return reward;
	}

	/**
	 * Gets the color
	 */
	public int getColor() {
		return color;
	}
}
