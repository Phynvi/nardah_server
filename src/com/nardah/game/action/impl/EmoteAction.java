package com.nardah.game.action.impl;

import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Handles performing an emote action
 * @author Daniel
 */
public final class EmoteAction extends Action<Player> {
	/**
	 * The animation identification.
	 */
	private final int animation;

	/**
	 * The graphic identification.
	 */
	private final int graphic;

	/**
	 * Constructs a new <code>EmoteAction</code>.
	 * @param player The player instance.
	 * @param animation The animation identification.
	 * @param graphic The graphic identification.
	 */
	public EmoteAction(Player player, int animation, int graphic) {
		super(player, 3, true);
		this.animation = animation;
		this.graphic = graphic;
	}

	@Override
	public void execute() {
		if(animation != -1) {
			getMob().animate(new Animation(animation));
		}

		if(graphic != -1) {
			getMob().graphic(new Graphic(graphic));
		}

		cancel();
	}

	@Override
	public String getName() {
		return "Emote";
	}

	@Override
	public boolean prioritized() {
		return false;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}
}