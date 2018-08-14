package io.battlerune.content.skillingpoints;

import io.battlerune.game.world.entity.mob.player.Player;

public class SkillingPoints {
	/**
	 * author adam trinity
	 */

	/**
	 * getters and setters, to access skilling points and set them.
	 **/

	int skillingPoints;
	private Player player;

	public void skillingPoints(Player player) {
		this.player = player;
	}

	public int getskillingPoints() {
		return skillingPoints;
	}

	public void setskillingPoints(int skillingPoints) {
		this.skillingPoints = skillingPoints;
	}

}
