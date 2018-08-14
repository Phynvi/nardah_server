package com.nardah.content.skill.impl.mining;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.activity.randomevent.RandomEventHandler;
import com.nardah.content.clanchannel.content.ClanTaskKey;
import com.nardah.content.prestige.PrestigePerk;
import com.nardah.Config;
import com.nardah.game.action.Action;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.task.Task;
import com.nardah.game.task.impl.ObjectReplacementEvent;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.object.GameObject;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.position.impl.SquareArea;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;

/**
 * Created by Daniel on 2017-12-18. Edited by Adam_#6723 on 2018-05-10.
 */
public class MiningAction extends Action<Player> {
	private final GameObject object;
	private final OreData ore;
	private final PickaxeData pickaxe;

	private Position preBankPos;
	private Position bankPosition = new Position(2909, 4831);
	private SquareArea bankArea = new SquareArea(2909, 4831, 2910, 4832);

	public MiningAction(Player player, GameObject object, OreData ore, PickaxeData pickaxe) {
		super(player, 3, false);
		this.object = object;
		this.ore = ore;
		this.pickaxe = pickaxe;
	}

	private boolean mine() {
		if(getMob().inventory.getFreeSlots() == 0) {
			if(ore.ore == OreData.RUNE_ESSENCE.ore) // I know it's dirty, but it saves me coding .equals()
			{
				preBankPos = getMob().getPosition();
				handleFullRuneEss();
			} else {
				getMob().dialogueFactory.sendStatement("You can't carry anymore ore.").execute();
			}
			return false;
		}

		getMob().animate(pickaxe.animation);

		if(Mining.success(getMob(), ore, pickaxe)) {
			if(object == null || !object.active()) {
				return false;
			}

			int amount = object.getGenericAttributes().get("ores", Integer.class);

			if(amount == -1) {
				getMob().resetAnimation();
				return false;
			}

			int harvest = ore.ore;
			boolean gem = harvest == -1;
			object.getGenericAttributes().modify("ores", amount - 1);

			if(gem) {
				harvest = Mining.GEM_ITEMS.next().getId();
			}

			getMob().inventory.add(harvest, 1);
			getMob().skills.addExperience(Skill.MINING, ore.experience * Config.MINING_MODIFICATION);
			RandomEventHandler.trigger(getMob());
			//            Pets.onReward(getMob(), PetData.GOLEM.getItem(), ore.pet);

			if(getMob().equipment.contains(21343)) {
				getMob().skills.addExperience(Skill.MINING, ore.experience * Config.MINING_MODIFICATION * 1.5);
				getMob().message("You are now Recieved 1.5x Mining Experience");
			}
			if(getMob().equipment.contains(21392)) {
				getMob().skills.addExperience(Skill.MINING, ore.experience * Config.MINING_MODIFICATION * 2);
				getMob().message("You are now recieving 2x Mining Experience.");
			}
			if(getMob().equipment.contains(21345)) {
				getMob().skills.addExperience(Skill.MINING, ore.experience * Config.MINING_MODIFICATION * 3.5);
				getMob().message("You are now recieving 3.5x Mining Experience.");
			}
			if(getMob().equipment.contains(13243)) {
				getMob().skills.addExperience(Skill.MINING, ore.experience * Config.MINING_MODIFICATION * 5);
				getMob().message("You are now recieving 5x Mining Experience.");
			}

			if(ore == OreData.RUNITE) {
				getMob().forClan(channel -> channel.activateTask(ClanTaskKey.RUNITE_ORES, getMob().getName()));
			}
			if(ore == OreData.IRON) {
				AchievementHandler.activate(getMob(), AchievementKey.MINE_A_IRON_ORE, 1);
				getMob().send(new SendMessage("You have Completed an Achievement " + "!"));
			}

			if(getMob().prestige.hasPerk(PrestigePerk.THE_ROCK) && RandomUtils.success(.10)) {
				getMob().inventory.addOrDrop(new Item(harvest, 1));
			}

			int base_chance = ore.ordinal() * 45;
			int modified_chance = /* getMob().equipment.isWearingChargedGlory() ? (int) (base_chance / 2.2) : */ base_chance;

			if(Utility.random(modified_chance) == 1) {
				if(getMob().inventory.getFreeSlots() != 0 && !gem) {
					Item item = Mining.GEM_ITEMS.next();
					getMob().inventory.add(item);
					getMob().send(new SendMessage("You have found " + Utility.getAOrAn(item.getName()) + " " + item.getName() + "."));
				}
			}

			if(object.active() && object.getGenericAttributes().get("ores", Integer.class) == -1 && ore.oreCount != -1) {
				this.cancel();
				getMob().resetAnimation();
				object.getGenericAttributes().set("ores", -1);
				getMob().skills.get(Skill.MINING).setDoingSkill(false);
				World.schedule(new ObjectReplacementEvent(object, ore.replacement, ore.respawn, () -> {
					object.getGenericAttributes().set("ores", ore.oreCount);
				}));
			}
		}
		return true;
	}

	private void handleFullRuneEss() {
		// getMob().getPlayer().masterMiner.open();
		getMob().interfaceManager.openNonDuplicate(49500);

		getMob().abortBot = false;
		World.schedule(new Task(1) {
			@Override
			protected void execute() {
				if(getMob().abortBot) {
					cancel();
				}

				if(getMob().inventory.getFreeSlots() == 0) {
					if(!bankArea.inArea(getMob().getPosition())) {
						getMob().walk(bankPosition); // Walk to the center
					} else {
						// Bank
						int count = getMob().inventory.computeAmountForId(OreData.RUNE_ESSENCE.ore);
						Item notedEss = new Item(new Item(OreData.RUNE_ESSENCE.ore).getNotedId(), count);

						getMob().inventory.remove(OreData.RUNE_ESSENCE.ore, count);
						getMob().inventory.add(notedEss);
					}
				} else {
					if(!getMob().near(preBankPos, 2)) {
						// Go back to the mines
						getMob().walkExactlyTo(preBankPos);
					} else {
						getMob().action.execute(new MiningAction(getMob(), object, ore, pickaxe));
						getMob().skills.get(Skill.MINING).setDoingSkill(true);
						getMob().message("You swing your pick at the rock...");
						cancel();
					}
				}

			}

		});
	}

	@Override
	protected boolean canSchedule() {
		return !getMob().skills.get(Skill.MINING).isDoingSkill();
	}

	@Override
	protected void onSchedule() {
		if(!object.getGenericAttributes().has("ores")) {
			object.getGenericAttributes().set("ores", ore.oreCount);
		}

		getMob().animate(pickaxe.animation);
	}

	@Override
	public void execute() {
		if(!getMob().skills.get(Skill.MINING).isDoingSkill()) {
			cancel();
			return;
		}
		if(object == null || !object.active() || object.getGenericAttributes() == null) {
			cancel();
			return;
		}

		if(!mine()) {
			cancel();
		}
	}

	@Override
	protected void onCancel(boolean logout) {
		getMob().resetFace();
		getMob().skills.get(Skill.MINING).setDoingSkill(false);
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public String getName() {
		return "mining-action";
	}
}