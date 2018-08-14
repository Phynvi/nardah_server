package io.battlerune.content.skill.impl.herblore;

import io.battlerune.Config;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.achievement.AchievementKey;
import io.battlerune.content.activity.randomevent.RandomEventHandler;
import io.battlerune.content.clanchannel.content.ClanTaskKey;
import io.battlerune.content.dialogue.ChatBoxItemDialogue;
import io.battlerune.content.event.impl.ItemInteractionEvent;
import io.battlerune.content.event.impl.ItemOnItemInteractionEvent;
import io.battlerune.game.Animation;
import io.battlerune.game.action.Action;
import io.battlerune.game.action.policy.WalkablePolicy;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendInputAmount;
import io.battlerune.util.StringUtils;
import io.battlerune.util.Utility;

/**
 * Handles the herblore skill.
 * @author Daniel
 */
public class Herblore extends Skill {

	/**
	 * Constructs a new <code>Herblore</code>.
	 */
	public Herblore(int level, double experience) {
		super(Skill.HERBLORE, level, experience);
	}

	@Override
	protected double modifier() {
		return Config.HERBLORE_MODIFICATION;
	}

	@Override
	protected boolean clickItem(Player player, ItemInteractionEvent event) {
		final int slot = event.getSlot();
		final Item item = player.inventory.get(slot);

		if(item == null) {
			return false;
		}

		if(!GrimyHerb.forId(item.getId()).isPresent()) {
			return false;
		}

		GrimyHerb herb = GrimyHerb.forId(item.getId()).get();

		if(getLevel() < herb.getLevel()) {
			final String name = herb.getGrimy().getName();
			player.dialogueFactory.sendStatement("You need a Herblore level of " + herb.getLevel() + " to clean " + Utility.getAOrAn(name) + " " + name + ".").execute();
			return false;
		}

		player.inventory.remove(item);
		player.inventory.add(herb.getClean());
		player.skills.addExperience(Skill.HERBLORE, herb.getExperience() * modifier());
		RandomEventHandler.trigger(player);
		return true;
	}

	@Override
	protected boolean useItem(Player player, ItemOnItemInteractionEvent e) {
		final int useSlot = e.getFirstSlot();
		final int withSlot = e.getSecondSlot();
		final Item use = player.inventory.get(useSlot);
		final Item with = player.inventory.get(withSlot);

		if(use == null || with == null) {
			return false;
		}

		final Potion potion = UnfinishedPotion.get(use, with) == null ? FinishedPotion.get(use, with) : UnfinishedPotion.get(use, with);

		if(potion == null) {
			return false;
		}

		if(getLevel() < potion.getLevel()) {
			final String name = potion.getProduct().getName();
			player.dialogueFactory.sendStatement("<col=369>You need a Herblore level of " + potion.getLevel() + " to make " + StringUtils.getAOrAn(name) + " " + name + " potion.").execute();
			return true;
		}

		if(player.inventory.computeAmountForId(use.getId()) == 1 || player.inventory.computeAmountForId(with.getId()) == 1) {
			player.inventory.set(useSlot > withSlot ? withSlot : useSlot, potion.getProduct(), true);
			player.inventory.set(useSlot < withSlot ? withSlot : useSlot, null, true);
			player.animate(new Animation(potion.getAnimation()));
		} else {
			ChatBoxItemDialogue.sendInterface(player, 1746, potion.getProduct(), 170);
			player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
				@Override
				public void firstOption(Player player) {
					player.action.execute(mix(player, potion, 1), true);
				}

				@Override
				public void secondOption(Player player) {
					player.action.execute(mix(player, potion, 5), true);
				}

				@Override
				public void thirdOption(Player player) {
					player.send(new SendInputAmount("Enter amount", 10, input -> player.action.execute(mix(player, potion, Integer.parseInt(input)), true)));
				}

				@Override
				public void fourthOption(Player player) {
					player.action.execute(mix(player, potion, 14), true);
				}
			};
		}
		return true;
	}

	/**
	 * Handles the potion mixing action.
	 */
	private Action<Player> mix(Player player, Potion potion, int amount) {
		return new Action<Player>(player, 2) {
			int ticks = 0;

			@Override
			public void execute() {
				if(!player.inventory.containsAll(potion.getIngredients())) {
					cancel();
					return;
				}

				player.animate(new Animation(potion.getAnimation()));
				player.inventory.removeAll(potion.getIngredients());
				player.inventory.add(potion.getProduct());
				player.skills.addExperience(Skill.HERBLORE, potion.getExperience() * modifier());
				AchievementHandler.activate(player, AchievementKey.POTION_MAKING, 1);
				RandomEventHandler.trigger(player);

				if(potion == FinishedPotion.SUPER_RESTORE) {
					getMob().forClan(channel -> channel.activateTask(ClanTaskKey.SUPER_RESTORE_POTION, getMob().getName()));
				}

				if(++ticks == amount) {
					cancel();
				}
			}

			@Override
			public String getName() {
				return "Herblore mix";
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
