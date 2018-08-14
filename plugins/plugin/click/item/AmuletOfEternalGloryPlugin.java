package plugin.click.item;

import io.battlerune.Config;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.event.impl.ItemClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;

public class AmuletOfEternalGloryPlugin extends PluginContext {

	private static final int[] AMULETS = { 19707 };

	public static void teleport(Player player, Item item, Position position, int index, boolean equipment) {
		// index = index + 1;

		/*
		 * if (equipment) {
		 * 
		 * 
		 * player.equipment.set(Equipment.AMULET_SLOT, new Item(AMULETS[index]), true);
		 * Teleportation.teleport(player, position); player.message("<col=7F007F>" +
		 * (index == 5 ? "Tip: Amulet of eternal glory has unlimited charges!" :
		 * "Your amulet of eternal glory has " +
		 * Utility.convertWord(index).toLowerCase() + "charge" + (index == 1 ? "" : "s")
		 * + " remaining.")); }
		 */
	}

	/*
	 * private int getIndex(int item) { int index = + 1; for (int amulet = 1; amulet
	 * < AMULETS.length; amulet++) { if (item == AMULETS[amulet]) { return amulet; }
	 * } return index; }
	 */

	@Override
	protected boolean thirdClickItem(Player player, ItemClickEvent event) {
		/*
		 * Item item = event.getItem(); int index = getIndex(item.getId());
		 */

		if (player.inventory.contains(19707)) {

			player.dialogueFactory.sendOption("Edgeville", () -> {

				if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
					player.message("@or2@you can't teleport above 30 wilderness");
				} else {
					Teleportation.teleport(player, Config.DEFAULT_POSITION, 20, () -> {
						player.send(new SendMessage("@or2@Welcome to the Edgeville, " + player.getName() + "!"));
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
		return true;

	}

}
