package plugin.click.item;

import com.nardah.content.DiceBag;
import com.nardah.content.activity.impl.zulrah.ZulrahActivity;
import com.nardah.content.consume.Anglerfish;
import com.nardah.content.skill.impl.slayer.Slayer;
import com.nardah.content.skill.impl.slayer.SlayerTask;
import com.nardah.content.skill.impl.woodcutting.BirdsNest;
import com.nardah.game.action.impl.SpadeAction;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendFadeScreen;
import com.nardah.net.packet.out.SendInputAmount;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendString;
import com.nardah.util.Utility;

public class ItemFirstClickPlugin extends PluginContext { // etest

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		switch (event.getItem().getId()) {
		case 12746:
			player.pkPoints += 3;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12746, 1);
			break;
		case 10028:
			if (player.inventory.getFreeSlots() <= 3) {
				player.message("You do not have enough inventory space to open this box!");
				return false;
			}
			if (Utility.random(1, 5) == 4) {
				player.inventory.add(995, 10000000);
				player.inventory.add(21314, 1);
				player.message("@gre@You were lucky and received 10 Million GP!");
			}
			if (Utility.random(1, 25) == 24) {
				player.inventory.add(995, 15000000);
				player.inventory.add(13719, 1);
				player.inventory.add(13686, 1);
				player.message("@blu@You were lucky and received 15 Million GP!");
			}
			if (Utility.random(1, 75) == 74) {
				player.inventory.add(995, 20000000);
				player.inventory.add(17160, 1);
				player.inventory.add(15300, 1);
				player.message("@red@You were lucky and received 20 Million GP!");
			}
			player.inventory.remove(10028, 1);
            player.message("you were unfortunate and did not recieve anything.");
			break;

		case 5020:
			if (player.inventory.contains(995, 1147000000)) {
				player.message("You can't claim this ticket, make some room!");
				return false;
			} else {
				player.inventory.add(995, 1000000000);
				player.message("You have just claimed 1 1Bil Ticket!");
				player.inventory.remove(5020, 1);
			}
			player.inventory.remove(5020, 1);
			player.message("@red@You were unfortunate on this day, " + player.getName() + "!");
			player.inventory.add(995, 5000000);
			player.inventory.add(1, 1);
			break;
		case 5021:
			if (player.inventory.contains(995, 1647000000)) {
				player.message("You can't claim this ticket, make some room!");
				return false;
			} else {
				player.inventory.add(995, 500000000);
				player.message("You have just claimed 1 500M Ticket!");
				player.inventory.remove(5021, 1);
			}
			break;
		case 12748:
			player.pkPoints += 5;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12748, 1);
			break;
		case 4079:
			player.animate(1460);
			break;
		case 12749:
			player.pkPoints += 7;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12749, 1);
			break;
		case 12750:
			player.pkPoints += 9;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12750, 1);
			break;
		case 12751:
			player.pkPoints += 11;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12751, 1);
			break;
		case 12752:
			player.pkPoints += 15;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12752, 1);
			break;
		case 12753:
			player.pkPoints += 18;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12753, 1);
			break;
		case 12754:
			player.pkPoints += 21;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12754, 1);
			break;
		case 12755:
			player.pkPoints += 23;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12755, 1);
			break;
		case 12756:
			player.pkPoints += 25;
			player.message("<img=14>You now have @red@" + player.getpkPoints() + " PVP Points!");
			player.inventory.remove(12756, 1);
			break;

		case 13441:
			Anglerfish.onAnglerEffect(player);
			break;

		case 12791:
			player.runePouch.open();
			break;
		case 4155: {
			Slayer slayer = player.slayer;
			SlayerTask task = slayer.getTask();
			player.send(new SendMessage(
					task == null ? "You currently don't have a task, visit Nieve in edgeville to be assigned one."
							: String.format(
									player.slayer.partner == null ? "You're assigned to kill %s; only %d more to go."
											: "You and " + player.slayer.partner.getName()
													+ " are assigned to kill %s; only %d more to go.",
									task.getName(), slayer.getAmount())));
		}
			break;

		case 995:

			player.send(new SendInputAmount("Enter the amount of coins you want to deposit:", 10,
					input -> player.bankVault.deposit(Integer.parseInt(input))));

			break;

		case 405: {
			int coins = Utility.random(50000, 75000);
			player.inventory.remove(405, 1);
			player.inventory.add(995, coins);
			player.message("You found " + Utility.formatDigits(coins) + " coins inside of the casket!");
			break;
		}
		case 12938:
			if (player.isTeleblocked()) {
				player.message("You are currently under the affects of a teleblock spell and can not teleport!");
				break;
			}

			player.locking.lock();
			player.send(new SendFadeScreen("You are teleporting to Zulrah's shrine...", 1, 3));
			World.schedule(5, () -> {
				player.move(new Position(2268, 3069, 0));
				ZulrahActivity.create(player);
				player.locking.unlock();
			});
			player.inventory.remove(12938);
			break;

		case 10834:

			int coins = 100000000;

			if (player.inventory.contains(995, 2100000000)) {
				player.message("you have max cash in your inventory, please bank your cash before claiming more.");
			} else {
				player.inventory.remove(10834, 1);
				player.inventory.add(995, coins);
				player.message("You have claimed the @gre@100 Mill " + "Coin Bag");
				player.animate(2109);
				player.graphic(1177);
			}
			break;

		case 10835:
			int coins1 = 500000000;
			if (player.inventory.contains(995, 1500000000)) {
				player.message("you have max cash in your inventory, please bank your cash before claiming more.");
			} else {
				player.inventory.remove(10835, 1);
				player.inventory.add(995, coins1);
				player.message("You have claimed the @gre@500 Mill " + "Coin Bag");
				player.animate(2109);
				player.graphic(1177);
			}
			break;

		/*
		 * case 11865: { player.inventory.remove(11865, 1); player.inventory.add(11864,
		 * 1); player.message("You have disassembled the slayer helmet."); break; } case
		 * 19639: { player.inventory.remove(19639, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; } case
		 * 19641: { player.inventory.remove(19641, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; } case
		 * 19643: { player.inventory.remove(19643, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; } case
		 * 19645: { player.inventory.remove(19645, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; } case
		 * 19647: { player.inventory.remove(19647, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; } case
		 * 19649: { player.inventory.remove(19649, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; } case
		 * 21264: { player.inventory.remove(21264, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; } case
		 * 21266: { player.inventory.remove(19639, 1); player.inventory.add(11864, 1);
		 * player.message("You have disassembled the slayer helmet."); break; }
		 */
		case 21034:
			if (player.unlockedPrayers.contains(Prayer.RIGOUR)) {
				player.dialogueFactory.sendStatement("You already have this prayer unlocked!").execute();
				return true;
			}

			player.inventory.remove(event.getItem());
			player.unlockedPrayers.add(Prayer.RIGOUR);
			player.dialogueFactory.sendStatement("You have learned the Rigour prayer!").execute();
			break;

		case 21079:
			if (player.unlockedPrayers.contains(Prayer.AUGURY)) {
				player.dialogueFactory.sendStatement("You already have this prayer unlocked!").execute();
				return true;
			}

			player.inventory.remove(event.getItem());
			player.unlockedPrayers.add(Prayer.AUGURY);
			player.dialogueFactory.sendStatement("You have learned the Augury prayer!").execute();
			break;

		case 2528:
			player.send(new SendString("Genie's Experience Lamp", 2810));
			player.send(new SendString("", 2831));
			player.interfaceManager.open(2808);
			break;

		/* Spade */
		case 952:
			player.action.execute(new SpadeAction(player), true);
			break;

		/* Dice bag */
		case 15098:
			DiceBag.roll(player, false);
			break;

		/* Looting bag. */
		case 11941:
			player.lootingBag.open();
			break;

		/* Birds nest. */
		case 5070:
		case 5071:
		case 5072:
		case 5073:
		case 5074:
			BirdsNest.search(player, event.getItem().getId());
			break;

		default:
			return false;

		}
		return true;
	}

}
