package com.nardah.game.action.impl;

import com.nardah.content.skill.SkillRepository;
import com.nardah.content.skill.impl.hunter.Hunter;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.util.Utility;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.mob.Mob;

/**
 * Teleports an entity to another part of the world.
 * @author Daniel
 */
public final class ImplingTeleportAction extends Action<Mob> {

	public ImplingTeleportAction(Mob entity) {
		super(entity, 180);
	}

	@Override
	protected void onSchedule() {
		getMob().transform(Utility.randomElement(SkillRepository.HUNTER_SPAWNS));
	}

	@Override
	public void execute() {
		getMob().move(getMob().spawnPosition);
		getMob().locking.unlock();
		Hunter.SPAWNS.put(getMob().id, getMob().spawnPosition);
		cancel();
	}

	@Override
	public String getName() {
		return "Impling teleport";
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
