package io.battlerune.game.action.impl;

import io.battlerune.content.ActivityLog;
import io.battlerune.content.CrystalChest;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.achievement.AchievementKey;
import io.battlerune.game.Animation;
import io.battlerune.game.action.Action;
import io.battlerune.game.action.policy.WalkablePolicy;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.net.packet.out.SendMessage;

/**
 * Handles opening a crystal chest.
 * @author Daniel
 */
public final class CrystalChestAction extends Action<Player> {

	private final GameObject object;

	public CrystalChestAction(Player player, GameObject object) {
		super(player, 1);
		this.object = object;
	}

	@Override
	protected void onSchedule() {
		getMob().locking.lock();
		getMob().inventory.remove(989, 1);
		getMob().animate(new Animation(881));
		getMob().send(new SendMessage("You attempt to unlock the chest..."));
	}

	@Override
	public void execute() {
		cancel();
	}

	@Override
	protected void onCancel(boolean logout) {
		getMob().locking.unlock();
		getMob().inventory.add(1631, 1);
		getMob().inventory.add(CrystalChest.getReward());
		AchievementHandler.activate(getMob(), AchievementKey.CRYSTAL_CHEST, 1);
		getMob().send(new SendMessage("...you find a few items inside of the chest."));
		getMob().activityLogger.add(ActivityLog.CRYSTAL_CHEST);
	}

	@Override
	public String getName() {
		return "Crystal chest";
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