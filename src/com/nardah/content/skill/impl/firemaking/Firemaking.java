package com.nardah.content.skill.impl.firemaking;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.activity.randomevent.RandomEventHandler;
import com.nardah.content.clanchannel.content.ClanTaskKey;
import com.nardah.content.prestige.PrestigePerk;
import com.nardah.Config;
import com.nardah.content.event.impl.ItemOnItemInteractionEvent;
import com.nardah.content.event.impl.ObjectInteractionEvent;
import com.nardah.game.action.Action;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.object.GameObject;
import com.nardah.util.RandomUtils;

/**
 * The firemaking skill.
 * @author Daniel
 */
public class Firemaking extends Skill {

	public Firemaking(int level, double experience) {
		super(Skill.FIREMAKING, level, experience);
	}

	@Override
	protected boolean useItem(Player player, ItemOnItemInteractionEvent event) {
		Item first = event.getFirst();
		Item second = event.getSecond();
		FiremakingData firemaking = null;
		Item item = null;
		if(!FiremakingData.forId(second.getId()).isPresent() && !FiremakingData.forId(first.getId()).isPresent())
			return false;
		if(first.getId() == 590) {
			firemaking = FiremakingData.forId(second.getId()).get();
			item = second;
		} else if(second.getId() == 590) {
			firemaking = FiremakingData.forId(first.getId()).get();
			item = first;
		}
		if(firemaking == null)
			return false;
		player.action.execute(new FiremakingAction(player, item, firemaking), true);
		return true;
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		if(event.getOpcode() != 0 || event.getObject().getId() != 5249) {
			return false;
		}

		if(player.getPosition().equals(event.getObject().getPosition())) {
			player.message("Please step out of the fire!");
			return true;
		}

		FiremakingData firemaking = null;

		for(Item item : player.inventory) {
			if(item != null && FiremakingData.forId(item.getId()).isPresent()) {
				firemaking = FiremakingData.forId(item.getId()).get();
				break;
			}
		}

		if(firemaking == null) {
			player.dialogueFactory.sendStatement("You have no logs in your inventory to add to this fire!").execute();
			return true;
		}

		if(player.skills.getMaxLevel(Skill.FIREMAKING) < firemaking.getLevel()) {
			player.message("You need a firemaking level of " + firemaking.getLevel() + " to light this log!");
			return true;
		}

		player.action.execute(bonfireAction(player, event.getObject(), firemaking, player.inventory.computeAmountForId(firemaking.getLog())));
		return true;
	}

	private Action<Player> bonfireAction(Player player, GameObject object, FiremakingData firemaking, int toBurn) {
		return new Action<Player>(player, 3) {
			int amount = toBurn;

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}

			@Override
			public String getName() {
				return "Bonfire action";
			}

			@Override
			protected void execute() {
				if(amount <= 0) {
					cancel();
					return;
				}
				if(!object.active()) {
					cancel();
					return;
				}
				if(!player.inventory.contains(firemaking.getLog())) {
					player.message("You have no more logs!");
					cancel();
					return;
				}
				player.inventory.remove(firemaking.getLog(), 1);
				player.animate(733);
				player.skills.addExperience(Skill.FIREMAKING, firemaking.getExperience() * Config.FIREMAKING_MODIFICATION);
				RandomEventHandler.trigger(player);

				if(firemaking == FiremakingData.WILLOW_LOG) {
					player.forClan(channel -> channel.activateTask(ClanTaskKey.BURN_WILLOW_LOG, getMob().getName()));
				}
				if(firemaking == FiremakingData.OAK_LOG) {
					AchievementHandler.activate(getMob(), AchievementKey.BURN_AN_OAK_LOG, 1);
					/*
					 * if(PlayerRight.isDonator(player) || PlayerRight.isSuper(player) ||
					 * PlayerRight.isDeveloper(player)) {
					 * player.setskillingPoints(player.getskillingPoints() + 2);
					 * player.message("<img=14>You now have @red@" + player.getskillingPoints() +
					 * " Skilling Points!"); } if(PlayerRight.isExtreme(player) ||
					 * PlayerRight.isElite(player)) {
					 * player.setskillingPoints(player.getskillingPoints() + 3);
					 * player.message("<img=14>You now have @red@" + player.getskillingPoints() +
					 * " Boss Points!"); } if(PlayerRight.isKing(player)) {
					 * player.setskillingPoints(player.getskillingPoints() + 4);
					 * player.message("<img=14>You now have @red@" + player.getskillingPoints() +
					 * " Boss Points!"); } else {
					 * player.setskillingPoints(player.getskillingPoints() + 1);
					 * player.message("<img=14>You now have @red@" + player.getskillingPoints() +
					 * " Boss Points!");
					 *
					 * }
					 */

				}
				if(firemaking == FiremakingData.YEW_LOG) {
					AchievementHandler.activate(getMob(), AchievementKey.BURN100YEW, 1);
					player.message("@red@You have completed an achievement");
				}

				if(firemaking == FiremakingData.NORMAL_LOG || firemaking == FiremakingData.OAK_LOG || firemaking == FiremakingData.WILLOW_LOG || firemaking == FiremakingData.MAPLE_LOG || firemaking == FiremakingData.MAGIC_LOG || firemaking == FiremakingData.ARCTIC_PINE_LOG) {
					AchievementHandler.activate(getMob(), AchievementKey.BURN100ANY, 1);
					player.message("@red@You have completed an achievement");
				}

				if(player.prestige.hasPerk(PrestigePerk.FLAME_ON) && RandomUtils.success(.25)) {
					player.inventory.remove(firemaking.getLog(), 1);
					player.skills.addExperience(Skill.FIREMAKING, firemaking.getExperience() * Config.FIREMAKING_MODIFICATION);
				}

				amount--;
			}

			@Override
			protected void onCancel(boolean logout) {
				//                player.resetAnimation();
			}
		};
	}
}
