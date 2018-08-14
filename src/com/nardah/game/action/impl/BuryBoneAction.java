package com.nardah.game.action.impl;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.skill.SkillAction;
import com.nardah.content.skill.impl.prayer.BoneData;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.Config;
import com.nardah.game.Animation;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;

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