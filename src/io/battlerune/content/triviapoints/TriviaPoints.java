package io.battlerune.content.triviapoints;

import io.battlerune.game.world.entity.actor.player.Player;

public class TriviaPoints {

	/**
	 * getters and setters, to access skilling points and set them.
	 **/

	int triviaPoints;
	private Player player;

	public void triviaPoints(Player player) {
		this.player = player;
		triviaPoints++;
	}

	public int gettriviaPoints() {
		return triviaPoints;
	}

	public void settriviaPoints(int triviaPoints) {
		this.triviaPoints = triviaPoints;
	}

	/**
	 * increments skilling points.
	 **/
	public static void incrementtriviaPoints(Player player, int triviaPoints, Object object) {
		player.skillingPoints++;
	}

}
