package plugin.click.npc;

import static io.battlerune.content.pet.PetData.JAD;

import java.util.concurrent.TimeUnit;

import io.battlerune.content.activity.impl.TutorialActivity;
import io.battlerune.content.consume.PotionData;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.dialogue.Expression;
import io.battlerune.content.dialogue.impl.ClanmasterDialogue;
import io.battlerune.content.dialogue.impl.ConstructionDialogue;
import io.battlerune.content.dialogue.impl.GamblerDialogue;
import io.battlerune.content.dialogue.impl.NieveDialogue;
import io.battlerune.content.dialogue.impl.RealmLordDialogue;
import io.battlerune.content.dialogue.impl.RoyalKingDialogue;
import io.battlerune.content.dialogue.impl.SailorKingDialouge;
import io.battlerune.content.dialogue.impl.VoteDialogue;
import io.battlerune.content.pet.Pets;
import io.battlerune.content.skill.impl.thieving.Thieving;
import io.battlerune.content.store.Store;
import io.battlerune.content.store.impl.SkillcapeStore;
import io.battlerune.game.event.impl.NpcClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.combat.strategy.player.special.CombatSpecial;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendRunEnergy;
import io.battlerune.net.packet.out.SendString;
import io.battlerune.util.Utility;

public class NpcFirstClickPlugin extends PluginContext {

	@Override
	protected boolean firstClickNpc(Player player, NpcClickEvent event) {
		final int id = event.getNpc().id;
		switch (id) {
		case 3893:
			player.dialogueFactory.sendNpcChat(3893, "Ahoy there!", "I'll note your Rune Essence!").execute();
			break;
		case 4925:
			player.dialogueFactory.sendNpcChat(4925, "Oh you saved me! Take this as a reward!")
					.sendItem("Reward", "Dusty Key", 1590).execute();
			player.inventory.add(1590, 1);
			break;

		case 7481:
			player.dialogueFactory.sendDialogue(new VoteDialogue());
			break;

		case 2152:
			Store.STORES.get("Bob's Brilliant Axes").open(player);
			break;

		case 5450:
			Store.STORES.get("Zaff's Superior Staffs").open(player);
			break;

		case 3115:
			Store.STORES.get("Zeke�s Superior Scimitars").open(player);
			break;

		case 5452:
			Store.STORES.get("Varrock Swordshop").open(player);
			break;

		case 5451:
			Store.STORES.get("Dommik's Crafting Store").open(player);
			break;

		case 5449:
			Store.STORES.get("Skiller Shop").open(player);
			break;

		case 5789:
			player.send(new SendString("Agility Ticket Exchange", 8383));
			player.interfaceManager.open(8292);
			break;

		case 2186:
			Store.STORES.get("The Tzhaar Tokkul Store").open(player);
			break;

		case 305:
			DialogueFactory factory1 = player.dialogueFactory;
			factory1.sendOption("Open Culinaromancer's Food Store",
					() -> Store.STORES.get("Culinaromancer's Food Store").open(factory1.getPlayer()),
					"Open Culinaromancer's Items Store",
					() -> Store.STORES.get("Culinaromancer's Items Store").open(factory1.getPlayer()), "Nevermind",
					factory1::clear);
			factory1.execute();

			break;

		case 2180: {
			DialogueFactory factory = player.dialogueFactory;
			factory.sendNpcChat(2180, "Would you like to sacrifice a firecape for a chance",
					"of getting the pet? Chances are 1/200.");
			factory.sendOption("Yes", () -> {
				factory.onAction(() -> {
					if (!player.inventory.contains(6570, 1)) {
						factory.sendNpcChat(2180, "You must have a firecape to sacrifice first!").execute();
						return;
					}

					player.inventory.remove(6570, 1);
					if (!Pets.onReward(player, JAD)) {
						factory.sendNpcChat(2180, "I'm sorry. The RNG was not on your side today!").execute();
					} else {
						factory.clear();
					}
				});
			}, "No", factory::clear);
			factory.execute();
			break;
		}

		/* Banker */
		case 394:
		case 395:
		case 2897:
		case 2898:
			player.bankPin.open();
			break;

		/* Mage bank */
		case 1600:
			player.bank.open();
			break;

		/* Nurse sarah */
		case 1152:
			int length = PlayerRight.isDonator(player) ? 1 : 2;
			if (!player.restoreDelay.elapsed(length, TimeUnit.MINUTES)) {
				player.dialogueFactory.sendNpcChat(id, "You can only do this once every " + length + " minutes!",
						"Time Passed: " + Utility.getTime(player.restoreDelay.elapsedTime())).execute();
				return true;
			}
			PotionData.onAntiPoisonEffect(player, false, 0);
			player.runEnergy = 100;
			player.send(new SendRunEnergy());
			player.skills.restoreAll();
			CombatSpecial.restore(player, 100);
			player.dialogueFactory.sendNpcChat(id, "Your health & special attack have been restored!").execute();
			player.restoreDelay.reset();
			break;

		case 506:
		case 507:
		case 513: // falador female
		case 512: // falador male
		case 1032:
			Store.STORES.get("The General Store").open(player);
			break;

		case 1199:
			Store.STORES.get("Chef's Choodle Oodle Store").open(player);
			break;

		/* Gnome trainer */
		case 6080:
			player.dialogueFactory.sendPlayerChat("Hello there.")
					.sendNpcChat(id, "This isn't a grannies' tea party, let's see some sweat", "human. Go! Go! Go!")
					.execute();
			break;

		/* Barbarian trainer */
		case 2153:
			player.dialogueFactory
					.sendNpcChat(id, "Haha welcome to my obstacle course. Have fun, but",
							"remember this isn't a child's playground you fat fuck.", "People have fucking died here.")
					.sendNpcChat(id, "The best way to train, is to go round the course in a", "clockwise direction.")
					.execute();
			break;

		/* Potion decanting */
		case 1146: {
//                DialogueFactory factory = player.dialogueFactory;
//                factory.sendNpcChat(id, "Hello, I am Joe the chemist.", "How may I help you?");
//                factory.sendOption(
//                        "Decant all potions (50,000 RT)",
//                        () -> factory.onAction(() -> Decanting.decant(player)),
//
//                        "Nevermind",
//                        () -> factory.onAction(player.interfaceManager::close));
//                factory.execute();
			break;
		}

		/* Ironman shop */
		case 311: {
			DialogueFactory factory = player.dialogueFactory;
			if (PlayerRight.isIronman(player) || PlayerRight.isDeveloper(player)) {
				factory.sendNpcChat(id, "Welcome my friend.", "How may I help you?")
						.sendOption("Claim armour", player.playerAssistant::claimIronmanArmour, "View shop",
								() -> factory.onAction(() -> Store.STORES.get("Ironman Store").open(player)),
								"Nevermind", factory::clear)
						.execute();
			} else {
				factory.sendNpcChat(id, "I have no buisness with you plebs, taking the easy route",
						"You're not an Ironman, you wimp.");

			}

		}
			break;

		/* Melee shop */
		case 3216: {
			DialogueFactory factory = player.dialogueFactory;
			factory.sendOption("Weapons", () -> Store.STORES.get("Melee Weapons").open(player), "Armour",
					() -> Store.STORES.get("Melee Armour").open(player)).execute();
		}
			break;

		/* Ranged shop */
		case 3217:
			Store.STORES.get("Range Store").open(player);
			break;

		/* Magic shop */
		case 3218:
			Store.STORES.get("Magic Store").open(player);
			break;
		/* PK Reward Shop 1 */
		case 841:
			Store.STORES.get("Pk Rewards Shop 1").open(player);
			break;
		/* Pure shop */
		case 5440:
			Store.STORES.get("Pure Store").open(player);
			break;

		/* Wise old man */
		case 4306:
			new SkillcapeStore().open(player);
			break;

		case 6773:
			player.lostUntradeables.open();
			break;

		/* Gamble shop */
		case 1011:
			Store.STORES.get("The Gamble Store").open(player);
			break;
		/* Gambler */
		case 1012:
			player.dialogueFactory.sendDialogue(new GamblerDialogue(event.getNpc()));
			break;
		/* Note Trader Shop */
		case 3189:
			Store.STORES.get("Money Bag Trader").open(player);
			break;
		/* Skilling shop */
		case 505:
			player.dialogueFactory.sendOption("Equipment", () -> {
				player.dialogueFactory.onAction(() -> Store.STORES.get("Skilling Store Equipment").open(player));
			}, "Farming", () -> {
				player.dialogueFactory.onAction(() -> Store.STORES.get("Farming Supplies").open(player));
			}, "Herblore", () -> {
				player.dialogueFactory.onAction(() -> Store.STORES.get("Herblore Supplies").open(player));
			}, "Miscellaneous", () -> {
				player.dialogueFactory.onAction(() -> Store.STORES.get("Miscellaneous Supplies").open(player));
			}).execute();
			break;

		/* Mac */
		case 6481: {
			DialogueFactory factory = player.dialogueFactory;
			if (!player.skills.isMaxed()) {
				factory.sendNpcChat(id, "Please speak to me once you are maxed in all 20 skills.").execute();
				return true;
			}

			factory.sendNpcChat(id, "Hello adventurer!", "Are you interested in purchasing a Max cape and",
					"hood for 2,500,000 gp?").sendOption("Yes", () -> {
						factory.onAction(() -> {
							if (!player.inventory.contains(new Item(995, 2_500_000))) {
								factory.sendNpcChat(id, "You need 2,500,000 gp to purchase this cape!").execute();
								return;
							}

							final Item[] ITEMS = { new Item(13281), new Item(13280) };
							if (!player.inventory.hasCapacityFor(ITEMS)) {
								factory.sendNpcChat(id, "You do not have enough inventory spaces for this!").execute();
								return;
							}

							player.inventory.remove(995, 2_500_000);
							player.inventory.addAll(ITEMS);
							factory.sendNpcChat(id, "Enjoy your new cape!").execute();
						});
					}, "No", () -> {
						factory.onAction(player.interfaceManager::close);
					}).execute();
		}
			break;

		/* Construction dialogue */
		case 5419:
			player.dialogueFactory.sendDialogue(new ConstructionDialogue());
			break;

		/* King Royal dialogue */
		case 5523:
			player.dialogueFactory.sendDialogue(new RoyalKingDialogue(0));
			break;

		/* Merchant King */
		case 5608:
			player.dialogueFactory.sendDialogue(new SailorKingDialouge(0));
			break;
		/* Clanmaster Dialogue. */
		case 1143:
			player.dialogueFactory.sendDialogue(new ClanmasterDialogue());
			break;

		/* Nieve Dialogue. */
		case 490:
		case 6797:
			player.dialogueFactory.sendDialogue(new NieveDialogue());
			break;

		/* Realm Lord */
		case 15:
			player.dialogueFactory.sendDialogue(new RealmLordDialogue());
			break;

		/* Makeover mage */
		case 1307:
			player.interfaceManager.open(3559);
			break;

		/* Thieving goods merchant */
		case 3439:
			Thieving.exchange(player);
			break;

		case 1755:
			Store.STORES.get("The Pest Control Store").open(player);
			break;

		case 5919:
			if (PlayerRight.isDonator(player)) {
				Store.STORES.get("Grace's Graceful Store").open(player);
			} else {
				player.dialogueFactory.sendStatement("You need to be a Donator or higher to access this store!")
						.execute();
			}
			break;

		case 306:
			player.dialogueFactory.sendNpcChat(306, "Hello #name, would you like to do the tutorial?");
			player.dialogueFactory.sendOption("Yes", () -> TutorialActivity.create(player), "No",
					() -> player.dialogueFactory.clear());
			player.dialogueFactory.execute();
			break;

		default:
			player.dialogueFactory.sendNpcChat(id, Expression.ANNOYED, "Please go away I'm busy.").execute();
		}
		return false;
	}

}
