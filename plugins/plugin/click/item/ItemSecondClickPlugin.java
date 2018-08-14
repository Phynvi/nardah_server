package plugin.click.item;

import io.battlerune.Config;
import io.battlerune.content.DiceBag;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.event.impl.ItemClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.net.packet.out.SendMessage;

public class ItemSecondClickPlugin extends PluginContext {

	@Override
	protected boolean secondClickItem(Player player, ItemClickEvent event) {
		switch (event.getItem().getId()) {
		case 15098:
			DiceBag.roll(player, true);
			break;
			
		case 4079:
			player.animate(1458);
			break;

		case 12414:
		case 12418:
		case 11802:
		case 11804:
		case 11806:
		case 11808:
		case 12436:
		case 12415:
		case 12417:
		case 20368:
		case 20370:
		case 20372:
		case 20374:
		case 12416:
		case 20000:
		case 20366:
		case 19720:
		case 19722:
			player.message("@red@To Dismantle this item, use a chisel on the item!");
			break;
		case 13342:
		case 20760:
		case 13280:

			// TODO ADAM TELEPORTS FOR THE CAPES!
			if (player.inventory.contains(13280)) {

				player.dialogueFactory.sendOption("Grotesque Guardians", () -> {

					if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
						player.message("@or2@you can't teleport above 30 wilderness");
					} else {
						Teleportation.teleport(player, Config.DEFAULT_POSITION, 20, () -> {
							player.send(new SendMessage(
									"@or2@Welcome to the Grotesque Guardians!, " + player.getName() + "!"));
						});
					}
				}, "Karamja", () -> {
					if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
						player.message("@or2@you can't teleport above 30 wilderness");
					} else {
						Teleportation.teleport(player, Config.KARAMJA, 20, () -> {
							player.send(new SendMessage("@or2@Welcome to the Karamja, " + player.getName() + "!"));
						});

					}
				}, "Dranyor Village", () -> {
					if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
						player.message("@or2@you can't teleport above 30 wilderness");
					} else {
						Teleportation.teleport(player, Config.DRAYNOR, 20, () -> {
							player.send(new SendMessage("@or2@Welcome to the Draynor, " + player.getName() + "!"));
						});
					}
				}, "Al-Kahrid", () -> {
					if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
						player.message("@or2@you can't teleport above 30 wilderness");
					} else {
						Teleportation.teleport(player, Config.AL_KHARID, 20, () -> {
							player.send(new SendMessage("@or2@Welcome to the Al Kharid, " + player.getName() + "!"));
						});
					}
				}, "Nowhere", player.interfaceManager::close).execute();
			}
			break;

		/* Enchanted Gem */
		case 4155:
			player.message(player.slayer.partner == null
					? "You don't have a slayer partner! Use the gem on another player to get one!"
					: "Your current slayer partner is: " + player.slayer.partner.getName());
			break;

		default:
			return false;
		}

		return true;
	}

}
