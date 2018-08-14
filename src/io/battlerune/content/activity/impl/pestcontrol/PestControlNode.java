package io.battlerune.content.activity.impl.pestcontrol;

import io.battlerune.content.ActivityLog;
import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.ActivityType;
import io.battlerune.content.activity.panel.Activity_Panel;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.util.Utility;

/**
 * A {@code PestControlNode} handles pest control activity events for a specific
 * to player.
 */
final class PestControlNode extends Activity {

	private PestControl pestControl;
	/**
	 * The player that owns this node.
	 */
	private final Player player;

	/**
	 * The amount of damage the player has dealt.
	 */
	protected int damge;

	/**
	 * Constructs a new {@link PestControlNode} object for a player.
	 */
	PestControlNode(PestControl pestControl, Player player) {
		super(1, pestControl.getInstance());
		this.pestControl = pestControl;
		this.player = player;
		this.damge = 0;
	}

	@Override
	protected void start() {
		player.move(PestControl.BOAT.transform(Utility.random(4), Utility.random(6)));
		player.dialogueFactory.sendNpcChat(1756, "Go with strength!", "Defend the void knight and destroy the portals!", "You are our only hope!").execute();
	}

	@Override
	public void finish() {
		getPanel().ifPresent(Activity_Panel::close);
		player.move(PestControl.END_POSITION);

		// if (damge == 0)

		if(pestControl.portalSet.isEmpty() && damge != 0 && pestControl.monsters.isEmpty()) {
			player.activityLogger.add(ActivityLog.PEST_CONTROL);
			player.setpestPoints(player.getpestPoints() + 3);
			player.dialogueFactory.sendNpcChat(1756, "You have beaten the minigame!", "You were rewarded with " + player.pestPoints + " pest control points.", "You now have: " + player.pestPoints + ".").execute();
		} else {
			player.dialogueFactory.sendNpcChat(1756, "You have failed!", "I am oh so disappointed in you.").execute();
		}
	}

	@Override
	public void cleanup() {
	}

	@Override
	public ActivityType getType() {
		return ActivityType.PEST_CONTROL;
	}
}
