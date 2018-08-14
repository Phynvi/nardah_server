package io.battlerune.game.action.impl;

import io.battlerune.Config;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.achievement.AchievementKey;
import io.battlerune.content.skill.SkillAction;
import io.battlerune.content.skill.impl.prayer.BoneData;
import io.battlerune.game.Animation;
import io.battlerune.game.action.policy.WalkablePolicy;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendMessage;

import java.util.Optional;

/**
 * Handles burying a bone.
 * @author Michael | Chex
 */
public final class BuryBoneAction extends SkillAction {
	private final int slot;
	private final Item item;
	private final BoneData bone;

	public BuryBoneAction(Player player, BoneData bone, int slot) {
		super(player, Optional.empty(), true);
		this.slot = slot;
		this.bone = bone;
		this.item = player.inventory.get(slot);
	}

	@Override
	public boolean canInit() {
		return getMob().skills.getSkills()[skill()].stopwatch.elapsed(1200);
	}

	@Override
	public void init() {

	}

	@Override
	public void onExecute() {
		getMob().animate(new Animation(827));
		Player player = getMob().getPlayer();
		player.inventory.remove(item, slot, true);
		player.skills.addExperience(skill(), experience());
		player.send(new SendMessage("You bury the " + item.getName() + "."));
		AchievementHandler.activate(player, AchievementKey.BURY_BONES, 1);
		cancel();
	}

	@Override
	public void onCancel(boolean logout) {
		getMob().skills.getSkills()[skill()].stopwatch.reset();
	}

	@Override
	public Optional<SkillAnimation> animation() {
		return Optional.empty();
	}

	@Override
	public double experience() {
		return bone.getExperience() * Config.PRAYER_MODIFICATION;
	}

	@Override
	public int skill() {
		return Skill.PRAYER;
	}

	@Override
	public String getName() {
		return "Bone bury";
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