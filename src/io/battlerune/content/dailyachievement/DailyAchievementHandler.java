package io.battlerune.content.dailyachievement;

import io.battlerune.content.writer.InterfaceWriter;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendBanner;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.net.packet.out.SendString;
import io.battlerune.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles the achievements.
 * @author Adam - discord = Adam_#6723
 */
public class DailyAchievementHandler {

	/**
	 * Activates the achievement for the individual player. Increments the completed
	 * amount for the player. If the player has completed the achievement, they will
	 * receive their reward.
	 */
	public static void activate(Player player, DailyAchievementKey achievement) {
		activate(player, achievement, 1);
	}

	/**
	 * Activates the achievement for the individual player. Increments the completed
	 * amount for the player. If the player has completed the achievement, they will
	 * receive their reward.
	 */
	public static void activate(Player player, DailyAchievementKey achievement, int increase) {
		final int current = player.playerAchievements1.computeIfAbsent(achievement, a -> 0);
		for(DailyAchievementList list : DailyAchievementList.values()) {
			if(list.getKey() == achievement) {
				if(current >= list.getAmount())
					return;
				player.playerAchievements1.put(achievement, current + increase);
				if(player.playerAchievements1.get(achievement) >= list.getAmount()) {
					player.bankVault.add(list.getDifficulty().getReward(), true);
					player.send(new SendBanner("You've completed an achievement", "You've been rewarded with " + Utility.formatDigits(list.getDifficulty().getReward()) + " gp", list.getDifficulty().getColor()));
				}
				InterfaceWriter.write(new DailyAchievementInterface(player));
			}
		}
	}

	/**
	 * Completes all the achievements for player (used for administrative purposes).
	 */
	public static void completeAll(Player player) {
		if(!completedAll(player)) {
			for(DailyAchievementList achievement : DailyAchievementList.values()) {
				if(!player.playerAchievements1.containsKey(achievement.getKey())) {
					player.playerAchievements1.put(achievement.getKey(), achievement.getAmount());
					continue;
				}
				player.playerAchievements1.replace(achievement.getKey(), achievement.getAmount());
			}
			player.send(new SendMessage("You have successfully mastered all Daily achievements."));
		}
	}

	/**
	 * Checks if the reward is completed.
	 */
	public static boolean completed(Player player, DailyAchievementList achievement) {
		if(!player.playerAchievements1.containsKey(achievement.getKey()))
			player.playerAchievements1.put(achievement.getKey(), 0);
		return player.playerAchievements1.get(achievement.getKey()) >= achievement.getAmount();
	}

	/**
	 * Gets the total amount of achievements completed.
	 */
	public static int getTotalCompleted(Player player) {
		int count = 0;
		for(DailyAchievementList achievement : DailyAchievementList.values()) {
			if(player.playerAchievements1.containsKey(achievement.getKey()) && completed(player, achievement))
				count++;
		}
		return count;
	}

	/**
	 * Handles getting the amount of achievements completed based on it's
	 * difficulty.
	 */
	public static int getDifficultyCompletion(Player player, DailyAchievementDifficulty difficulty) {
		int count = 0;
		for(DailyAchievementList achievement : DailyAchievementList.values()) {
			if(player.playerAchievements1.containsKey(achievement.getKey()) && achievement.getDifficulty() == difficulty && completed(player, achievement))
				count++;
		}
		return count;
	}

	/**
	 * Handles getting the amount of achievements based on the difficulty.
	 */
	public static int getDifficultyAchievement(DailyAchievementDifficulty difficulty) {
		int count = 0;
		for(DailyAchievementList achievement : DailyAchievementList.values()) {
			if(achievement.getDifficulty() == difficulty)
				count++;
		}
		return count;
	}

	/**
	 * Checks if a player has completed all the available achievements.
	 */
	public static boolean completedAll(Player player) {
		return getTotalCompleted(player) == DailyAchievementList.getTotal();
	}

	private static List<DailyAchievementList> ach = new ArrayList<DailyAchievementList>();// redo this - harryl - to
	// store button ids! leave
	// this here will redo that
	// part later
	private static DailyAchievementList current;
	private static int count = 37331;

	public static DailyAchievementList getCurrent() {
		return current;
	}

	public static void setCurrent(DailyAchievementList current) {
		DailyAchievementHandler.current = current;
	}

	public static void rotateDailyAchievements(Player player, int rotate_amount) {
		Random random = new Random();
		for(int i = 0; i < rotate_amount; i++) {
			setCurrent(DailyAchievementList.values()[random.nextInt(DailyAchievementList.values().length)]);
			if(!ach.contains(getCurrent())) {
				ach.add(getCurrent());
			} else {
				setCurrent(DailyAchievementList.values()[random.nextInt(DailyAchievementList.values().length)]);
			}
		}
		setCurrent(null);// btw the achievements are in order like from numbers to a-z
	}

	public static void sendDailyAchievements(Player player, int amount) {
		refreshDailyAchievementsInterface(player);
		rotateDailyAchievements(player, amount);
		ach.stream().sorted().forEachOrdered(achievement -> {
			if(achievement != null) {
				player.send(new SendString(achievement.getTask(), count++));
			}
		});
	}

	public static void refreshDailyAchievementsInterface(Player player) {
		ach.clear();
		for(int i = 0; i < 50; i++) {
			player.send(new SendString("", 37331 + i));
		}
		System.out.println("[Daily Achievements] Refreshed tab!");
	}
}
