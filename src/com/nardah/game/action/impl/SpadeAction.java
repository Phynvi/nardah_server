package com.nardah.game.action.impl;

import com.nardah.content.activity.impl.barrows.BarrowsUtility;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.Animation;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

/**
 * Handles digging with a spade.
 * @author Daniel.
 */
public final class SpadeAction extends Action<Player> {

	public SpadeAction(Player player) {
		super(player, 2, false);
	}

	@Override
	protected void onSchedule() {
		getMob().movement.reset();
		getMob().animate(new Animation(830));
		getMob().send(new SendMessage("You start digging..."));
	}

	@Override
	public void execute() {
		boolean found = false;

		if(BarrowsUtility.teleportPlayer(getMob()))
			found = true;

		if(!found)
			getMob().send(new SendMessage("You found nothing of interest."));
		cancel();
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public boolean prioritized() {
		return false;
	}

	@Override
	public String getName() {
		return "Spade Action";
	}
}