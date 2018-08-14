package com.nardah.content.skill.impl.woodcutting;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.activity.randomevent.RandomEventHandler;
import com.nardah.content.clanchannel.content.ClanTaskKey;
import com.nardah.content.prestige.PrestigePerk;
import com.nardah.Config;
import com.nardah.content.pet.PetData;
import com.nardah.content.pet.Pets;
import com.nardah.game.action.Action;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.task.impl.ObjectReplacementEvent;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.game.world.object.GameObject;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.RandomUtils;

/**
 * Handles the woodcutting action event.
 * @author Daniel
 */
public class WoodcuttingAction extends Action<Player> {
	private final GameObject object;
	private final TreeData tree;
	private final AxeData axe;

	WoodcuttingAction(Player mob, GameObject object, TreeData tree, AxeData axe) {
		super(mob, 3, false);
		this.object = object;
		this.tree = tree;
		this.axe = axe;
	}

	private boolean chop() {
		if(getMob().inventory.getFreeSlots() == 0) {
			getMob().dialogueFactory.sendStatement("You can't carry anymore log.").execute();
			return false;
		}

		getMob().animate(axe.animation);
		if(Woodcutting.success(getMob(), tree, axe)) {
			if(object == null || !object.active()) {
				return false;
			}

			int amount = object.getGenericAttributes().get("logs", Integer.class);

			if(amount == -1) {
				getMob().resetAnimation();
				return false;
			}

			object.getGenericAttributes().modify("logs", amount - 1);

			if(object.getId() != 2092) // AFK tree doesn't give birds nests, randoms
			{
				BirdsNest.drop(getMob());
				RandomEventHandler.trigger(getMob());
				getMob().send(new SendMessage("You get some " + ItemDefinition.get(tree.item).getName() + "."));
			} else {
				// bonus logs cus why not
				int bonus = RandomUtils.inclusive(0, 6);
				for(int i = 0; i < bonus; i++) {
					getMob().forClan(channel -> channel.activateTask(ClanTaskKey.AFK_LOG, getMob().getName()));
					getMob().inventory.add(tree.item, 1);
					getMob().inventory.refresh();
				}
				object.getGenericAttributes().modify("logs", amount - 1 - bonus);
			}

			Pets.onReward(getMob(), PetData.BEAVER.getItem(), tree.petRate);
			getMob().inventory.add(tree.item, 1);
			getMob().skills.addExperience(Skill.WOODCUTTING, tree.experience * Config.WOODCUTTING_MODIFICATION);

			if(getMob().prestige.hasPerk(PrestigePerk.DOUBLE_WOOD) && RandomUtils.success(.15)) {
				getMob().inventory.addOrDrop(new Item(tree.item, 1));
			}

			if(getMob().equipment.contains(13241)) {
				getMob().skills.addExperience(Skill.WOODCUTTING, tree.experience * Config.MINING_MODIFICATION * 5);
				getMob().message("You are now recieving 5x Woodcutting Experience.");
			}

			if(tree == TreeData.OAK_TREE || tree == TreeData.OAK_TREE) {
				AchievementHandler.activate(getMob(), AchievementKey.CUT_A_OAK_TREE, 1);
			}
			/** CUT 100 TREES **/
			if(tree == TreeData.OAK_TREE || tree == TreeData.ACHEY_TREE || tree == TreeData.NORMAL_TREE || tree == TreeData.WILLOW_TREE || tree == TreeData.WILLOW_TREE1 || tree == TreeData.YEW_TREE) {
				AchievementHandler.activate(getMob(), AchievementKey.CUT100TREES, 1);
			}

			if(tree == TreeData.WILLOW_TREE || tree == TreeData.WILLOW_TREE1) {
				getMob().forClan(channel -> channel.activateTask(ClanTaskKey.CHOP_WILLOW_LOG, getMob().getName()));
			}
			if(tree == TreeData.YEW_TREE) {
				getMob().forClan(channel -> channel.activateTask(ClanTaskKey.YEW_LOG, getMob().getName()));
				AchievementHandler.activate(getMob(), AchievementKey.CUT_YEWTREE, 1);

			}
			if(tree == TreeData.MAGIC_TREE) {
				getMob().forClan(channel -> channel.activateTask(ClanTaskKey.MAGIC_LOG, getMob().getName()));
				AchievementHandler.activate(getMob(), AchievementKey.CUT_MAGICTREE, 1);
			}

			getMob().forClan(channel -> channel.activateTask(ClanTaskKey.CHOP_ANY_LOG, getMob().getName()));

			if(object.active() && object.getGenericAttributes().get("logs", Integer.class) <= -1) {
				this.cancel();
				getMob().resetAnimation();
				AchievementHandler.activate(getMob(), AchievementKey.WOODCUTTING, 1);
				object.getGenericAttributes().set("logs", -1);
				World.schedule(new ObjectReplacementEvent(object, tree.replacement, tree.respawn, () -> {
					object.getGenericAttributes().set("logs", tree.logs);
				}));
			}
		}
		return true;
	}

	@Override
	protected boolean canSchedule() {
		return !getMob().skills.get(Skill.WOODCUTTING).isDoingSkill();
	}

	@Override
	protected void onSchedule() {
		if(!object.getGenericAttributes().has("logs")) {
			object.getGenericAttributes().set("logs", tree.logs);
		}

		getMob().animate(axe.animation);
	}

	@Override
	public void execute() {
		if(!getMob().skills.get(Skill.WOODCUTTING).isDoingSkill()) {
			cancel();
			return;
		}
		if(object == null || !object.active() || object.getGenericAttributes() == null) {
			cancel();
			return;
		}

		if(!chop()) {
			cancel();
		}
	}

	@Override
	protected void onCancel(boolean logout) {
		getMob().resetFace();
		getMob().skills.get(Skill.WOODCUTTING).setDoingSkill(false);
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public String getName() {
		return "woodcutting-action";
	}
}
