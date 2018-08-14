package io.battlerune.content.skill.impl.thieving;

import io.battlerune.Config;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.achievement.AchievementKey;
import io.battlerune.content.activity.randomevent.RandomEventHandler;
import io.battlerune.content.clanchannel.content.ClanTaskKey;
import io.battlerune.content.dialogue.Expression;
import io.battlerune.content.event.impl.NpcInteractionEvent;
import io.battlerune.content.event.impl.ObjectInteractionEvent;
import io.battlerune.game.Animation;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.data.LockType;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.game.world.position.Area;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

/**
 * Handles the thieving skill.
 * @author Daniel
 */
public class Thieving extends Skill {

	public Thieving(int level, double experience) {
		super(Skill.THIEVING, level, experience);
	}

	/**
	 * The failure rate for pickpocketing.
	 */
	private int failureRate(PickpocketData pickpocket, Player player) {
		double f1 = pickpocket.getLevel() / 10;
		double f2 = 100 / ((player.skills.getMaxLevel(Skill.THIEVING) + 1) - pickpocket.getLevel());
		return (int) Math.floor((f2 + f1) / 2);
	}

	@Override
	protected double modifier() {
		return Config.THIEVING_MODIFICATION;
	}

	@Override
	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		if(event.getOpcode() != 1) {
			return false;
		}

		Npc npc = event.getNpc();
		PickpocketData pickpocket = PickpocketData.forId(npc.id);

		if(pickpocket == null) {
			return false;
		}

		if(player.skills.get(THIEVING).isDoingSkill()) {
			return true;
		}

		if(player.skills.getLevel(Skill.THIEVING) < pickpocket.getLevel()) {
			player.send(new SendMessage("You need a thieving level of " + pickpocket.getLevel() + " to do this!"));
			return true;
		}

		boolean failed = Utility.random(100) < failureRate(pickpocket, player);

		player.animate(new Animation(881));
		player.locking.lock(1, LockType.MASTER);
		player.skills.get(THIEVING).setDoingSkill(true);
		player.send(new SendMessage("You attempt to pickpocket the " + npc.getName() + "..."));

		World.schedule(2, () -> {
			if(failed) {
				npc.animate(new Animation(422));
				npc.speak("What do you think you're doing?");
				player.locking.lock(pickpocket.getStun(), LockType.STUN);
				player.damage(new Hit(Utility.random(pickpocket.getDamage())));
				player.send(new SendMessage("You failed to pickpocketing the " + npc.getName() + "."));
				return;
			}

			double experience = Area.inDonatorZone(player) ? pickpocket.getExperience() * 2 : pickpocket.getExperience();

			player.skills.get(THIEVING).setDoingSkill(false);
			player.inventory.add(Utility.randomElement(pickpocket.getLoot()));
			player.send(new SendMessage("You have successfully pickpocket the " + npc.getName() + "."));
			player.skills.addExperience(Skill.THIEVING, experience * Config.THIEVING_MODIFICATION);
			RandomEventHandler.trigger(player);
		});
		return true;
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		if(event.getOpcode() == 0 && event.getObject().getId() == 7236) {
			WallSafe.thieve(player);
			return true;
		}

		if(event.getOpcode() != 1) {
			return false;
		}

		GameObject object = event.getObject();

		if(!StallData.forId(object.getId()).isPresent()) {
			return false;
		}

		StallData stall = StallData.forId(object.getId()).get();

		if(!player.inventory.hasCapacityFor(stall.getItem())) {
			player.send(new SendMessage("You need at least 1 free inventory space to do this!"));
			return true;
		}

		if(player.skills.get(THIEVING).getLevel() < stall.getLevel()) {
			player.send(new SendMessage("You need a thieving level of " + stall.getLevel() + " to steal from this stall."));
			return true;
		}

		if(player.skills.get(THIEVING).isDoingSkill()) {
			return true;
		}

		int base_chance = 50;
		int modified_chance = (int) (PlayerRight.isDonator(player) ? base_chance * 2.3 : base_chance);
		boolean failed = Utility.random(modified_chance) == 0;

		player.animate(new Animation(832));
		player.locking.lock(1, LockType.MASTER);
		player.skills.get(THIEVING).setDoingSkill(true);

		World.schedule(3, () -> {
			double experience = stall.getExperience() * Config.THIEVING_MODIFICATION;
			double newExperience = Area.inDonatorZone(player) ? experience * 2 : experience;

			if(failed) {
				player.damage(new Hit(Utility.random(3, 6)));
				player.speak("Ouch!");
				player.send(new SendMessage("You failed to thieve from the stall and were instead inflicted pain!"));
				return;
			}

			player.inventory.add(stall.getItem());

			if(PlayerRight.isSuper(player) && Utility.random(0, 10) > 8) {
				player.inventory.addOrDrop(stall.getItem());
			}

			player.skills.get(THIEVING).setDoingSkill(false);
			player.skills.addExperience(THIEVING, newExperience);
			AchievementHandler.activate(player, AchievementKey.STEAL_FROM_STALL, 1);

			player.forClan(channel -> channel.activateTask(ClanTaskKey.THIEVING_STALL, player.getName()));
			RandomEventHandler.trigger(player);
		});
		return true;
	}

	public static void exchange(Player player) {
		int amount = 0, count = 0;

		for(Item item : player.inventory.toArray()) {
			if(StallData.isReward(item) && player.inventory.remove(item, -1, false)) {
				amount += StallData.getValue(item);
				count++;
			}
		}

		if(count == 0) {
			player.dialogueFactory.sendNpcChat(3439, Expression.ANGRY, "You do not have any items of which I am interested", "in purchasing.").execute();
			return;
		}

		if(player.inventory.add(new Item(Config.CURRENCY, amount))) {
			player.send(new SendMessage("You have exchanged " + count + " goods for " + Utility.formatDigits(amount) + " coins."));
			player.inventory.refresh();
			return;
		}

		player.send(new SendMessage("You have no items which the merchant would like to purchase."));
	}
}
