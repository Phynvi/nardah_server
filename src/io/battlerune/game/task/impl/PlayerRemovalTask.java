package io.battlerune.game.task.impl;

import io.battlerune.content.activity.Activity;
import io.battlerune.game.task.TickableTask;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.persist.PlayerSerializer;
import io.battlerune.util.Stopwatch;

import java.util.concurrent.TimeUnit;

public class PlayerRemovalTask extends TickableTask {
	
	private final Player player;
	private final Stopwatch stopwatch = Stopwatch.start();
	private final boolean force;
	private boolean flag;
	
	public PlayerRemovalTask(Player player, boolean force) {
		super(true, 0);
		this.player = player;
		this.force = force;
	}
	
	@Override
	protected void tick() {
		
		// wait till player death task is finished
		if(player.isDead()) {
			return;
		}
		
		// player is in combat for too long
		if(player.getCombat().isUnderAttack() && stopwatch.elapsedTime(TimeUnit.SECONDS) < 60) {
			return;
		}
		
		// if a player is in an activity they can logout if the activity lets them or
		// unless 60 seconds has elapsed
		if(Activity.evaluate(player, it -> !it.canLogout(player)) && stopwatch.elapsedTime(TimeUnit.SECONDS) > 60) {
			return;
		}
		
		if(!flag) {
			PlayerSerializer.save(player);
			flag = true;
			return;
		}
		
		// make sure the players account has saved first, player saving is on a
		// different thread so its gonna take
		// a few seconds
		if(player.saved.get()) {
			cancel();
		}
		
	}
	
	@Override
	protected void onCancel(boolean logout) {
		player.unregister();
	}
	
}
