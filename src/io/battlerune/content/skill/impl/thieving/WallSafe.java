package io.battlerune.content.skill.impl.thieving;

import io.battlerune.Config;
import io.battlerune.game.Animation;
import io.battlerune.game.action.Action;
import io.battlerune.game.action.policy.WalkablePolicy;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

/**
 * Handles the wall safe thieving.
 * @author Daniel
 */
public class WallSafe {

	/**
	 * Holds all the rewards.
	 */
	private static final Item REWARD[] = {new Item(1617), new Item(1619), new Item(1621), new Item(1623), new Item(1623), new Item(995, 20), new Item(995, 40)};

	/**
	 * Handles thieving from the wall safe.
	 */
	static void thieve(Player player) {
		if(player.skills.getMaxLevel(Skill.THIEVING) < 50) {
			player.dialogueFactory.sendStatement("You need a thieving level of 50 to do this!").execute();
			return;
		}
		if(player.inventory.remaining() == 0) {
			player.dialogueFactory.sendStatement("You do not have inventory spaces to do this!").execute();
			return;
		}
		if(player.skills.get(Skill.THIEVING).isDoingSkill()) {
			return;
		}
		player.action.execute(crack(player), true);
	}

	/**
	 * Gets the thieving rate.
	 */
	private static int rate(Player player) {
		boolean stethoscope = player.inventory.contains(new Item(5560));
		int calculation = stethoscope ? Utility.random(5) : (Utility.random(11) + 20);
		int calculated = (10 - (int) Math.floor(player.skills.getMaxLevel(Skill.THIEVING) / 10) + calculation) / 2;
		return calculated <= 0 ? 1 : (calculated > 10 ? 10 : calculated);
	}

	/**
	 * The wallsafe fail chance.
	 */
	public static int chance(Player player) {
		return (Utility.random((int) Math.floor(player.skills.getMaxLevel(Skill.THIEVING / 10) + 1)));
	}

	/**
	 * The wallsafe crack action.
	 */
	private static Action<Player> crack(Player player) {
		return new Action<Player>(player, 3, true) {
			int rate = rate(player);
			int tick = 0;

			@Override
			public void execute() {
				if(tick++ != rate) {
					player.animate(new Animation(881));
					return;
				}

				if(chance(player) == 0) {
					player.animate(new Animation(404));
					player.send(new SendMessage("You slip and trigger a trap!"));
					player.damage(new Hit(Utility.random(1, 5)));
					cancel();
					return;
				}

				player.send(new SendMessage("You get some loot."));
				player.inventory.add(Utility.randomElement(REWARD));
				player.skills.addExperience(Skill.THIEVING, 100 * Config.THIEVING_MODIFICATION);
				cancel();
			}

			@Override
			protected void onSchedule() {
				player.skills.get(Skill.THIEVING).setDoingSkill(true);
				player.send(new SendMessage("You attempt to crack the safe... "));
			}

			@Override
			protected void onCancel(boolean logout) {
				player.skills.get(Skill.THIEVING).setDoingSkill(false);
			}

			@Override
			public String getName() {
				return "Wallsafe crack";
			}

			@Override
			public boolean prioritized() {
				return false;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}
		};
	}
}
