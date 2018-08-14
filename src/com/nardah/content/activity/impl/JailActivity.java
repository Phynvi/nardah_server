package com.nardah.content.activity.impl;

import com.nardah.Config;
import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

public class JailActivity extends Activity {
	private final Player player;

	private JailActivity(Player player) {
		super(30, Actor.DEFAULT_INSTANCE_HEIGHT);
		this.player = player;
	}

	public static JailActivity create(Player player) {
		JailActivity activity = new JailActivity(player);
		player.move(Config.JAIL_ZONE);
		activity.add(player);
		activity.resetCooldown();
		player.setVisible(true);
		return activity;
	}

	@Override
	protected void start() {
		if(!player.punishment.isJailed()) {
			finish();
		}
	}

	@Override
	public void onDeath(Actor actor) {
		player.move(Config.JAIL_ZONE);
		player.message("BAM! YOU'RE BACK!");
	}

	@Override
	public boolean canTeleport(Player player) {
		player.message("You are jailed you douche! until staff member says otherwise.");
		player.message("ask a staff to check your jail duration.");
		return false;
	}

	@Override
	public void onRegionChange(Player player) {
		player.move(Config.JAIL_ZONE);
	}

	@Override
	public void finish() {
		remove(player);
		player.move(Config.DEFAULT_POSITION);
		player.message("Time's up! You are free to go, hope you learn from your mistakes.");
	}

	@Override
	public void cleanup() {

	}

	@Override
	public ActivityType getType() {
		return ActivityType.JAIL;
	}
}
