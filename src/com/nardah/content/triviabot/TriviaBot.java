package com.nardah.content.triviabot;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.clanchannel.content.ClanTaskKey;
import com.nardah.Config;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

import java.util.*;

/**
 * Manages the trivia bot system.
 * @author Daniel
 */
public class TriviaBot {
	
	private static long answeredAt;
	private static ArrayList<String> answeredPlayers = new ArrayList<>();
	
	/**
	 * Holds all the bot data.
	 */
	private final static Set<TriviaBotData> DATA = new HashSet<>();
	
	/**
	 * The current question/answer set.
	 */
	private static TriviaBotData CURRENT = null;
	
	/**
	 * Color of the TriviaBot messages.
	 */
	private static final String COLOR = "<col=354CE6>";
	
	/**
	 * Declares the TriviaBot data.
	 */
	public static void declare() {
		Collections.addAll(DATA, TriviaBotData.values());
	}
	
	/**
	 * Assigns a new question
	 */
	public static void assign() {
		answeredAt = Long.MAX_VALUE;
		CURRENT = Utility.randomElement(DATA);
		answeredPlayers.clear();
		String toSend = CURRENT.getQuestion().replaceAll("%.*", "");
		if(toSend.length() < 74) {
			World.sendMessage(COLOR + "TriviaBot: </col>" + toSend, player -> player.settings.triviaBot);
		} else {
			World.sendMessage(COLOR + "TriviaBot: </col>" + toSend.substring(0, 74), player -> player.settings.triviaBot);
			World.sendMessage(toSend.substring(74), player -> player.settings.triviaBot);
		}
	}
	
	/**
	 * Handles player answering the question
	 */
	public static void answer(Player player, String answer) {
		if(CURRENT.isRemoveSpacesInAnswer()) {
			answer = answer.replaceAll("\\s", "");
		}
		if(!player.settings.triviaBot) {
			return;
		}
		if(CURRENT == null) {
			player.send(new SendMessage(COLOR + "TriviaBot: </col>There is no question currently assigned!"));
			return;
		}
		if(Arrays.stream(Config.BAD_STRINGS).anyMatch(answer::contains)) {
			player.send(new SendMessage(COLOR + "TriviaBot: </col>You think you're funny, don't you? Guess what? You ain't."));
			return;
		}
		String finalAnswer = answer;
		if(Arrays.stream(CURRENT.getAnswers()).anyMatch(a -> a.equalsIgnoreCase(finalAnswer))) {
			answered(player, answer);
			player.forClan(channel -> channel.activateTask(ClanTaskKey.TRIVIA_ANSWER_KEY, player.getName()));
			return;
		}
		System.out.println("Wrong: You said [" + finalAnswer + "] when it was one of:" + Arrays.toString(CURRENT.getAnswers()));
		if(Utility.random(3) == 0) {
			player.speak("Oh darn it! I just entered a wrong trivia answer!");
		}
		player.send(new SendMessage(COLOR + "TriviaBot: </col>Sorry, the answer you have entered is incorrect! Try again!"));
	}
	
	/**
	 * Handles player answering the question successfully
	 */
	private static void answered(Player player, String answer) {
		if(answeredPlayers.contains(player.getName())) {
			player.message("You've already answered this question!");
			return;
		}
		
		if(PlayerRight.isDonator(player) || PlayerRight.isSuper(player)) {
			player.SetTriviaPoints(player.getTriviaPoints() + 2);
			player.message("<img=14>You now have @red@" + player.getTriviaPoints() + " Trivia Points!");
			
		} else if(PlayerRight.isExtreme(player) || PlayerRight.isElite(player)) {
			player.SetTriviaPoints(player.getTriviaPoints() + 3);
			player.message("<img=14>You now have @red@" + player.getTriviaPoints() + " Trivia Points!");
			
		} else if(PlayerRight.isKing(player)) {
			player.SetTriviaPoints(player.getTriviaPoints() + 4);
			player.message("<img=14>You now have @red@" + player.getTriviaPoints() + " Trivia Points!");
		} else {
			player.SetTriviaPoints(player.getTriviaPoints() + 1);
			player.message("<img=14>You now have @red@" + player.getTriviaPoints() + " Trivia Points!");
		}
		
		if(player.triviaPoints >= 100) {
			AchievementHandler.activate(player, AchievementKey.TRIVIABOT_II, 1);
		}
		
		String color = player.right.getColor();
		int reward = Utility.random(30000, 50000);
		
		if(answeredAt == Long.MAX_VALUE) {
			World.sendMessage(COLOR + "TriviaBot: <col=" + color + ">" + player.getName() + "</col> has answered the question correctly! Trivia will go on for another 10 seconds.");
			//        World.sendMessage(COLOR + "TriviaBot: <col=" + color + ">" + player.getName() + "</col> Time is up! Answer: " + COLOR + Utility.capitalizeSentence(answer) + "</col>.");
			answeredAt = System.currentTimeMillis();
		}
		
		if(answeredAt > System.currentTimeMillis() + 10000) {
			player.message("Sorry! There is no trivia running right now.");
			CURRENT = null;
			return;
		} else {
			player.bankVault.add(reward);
			AchievementHandler.activate(player, AchievementKey.TRIVIABOT, 1);
			player.send(new SendMessage(Utility.formatDigits(reward) + " coins were added into your bank vault."));
		}
		
		answeredPlayers.add(player.getName());
	}
}
