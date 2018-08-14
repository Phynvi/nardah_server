package com.nardah.content.skillingpoints;

import com.nardah.game.world.entity.actor.player.Player;

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
