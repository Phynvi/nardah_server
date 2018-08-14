package plugin.click.button;

import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.skill.impl.magic.Spellbook;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;

/**
 * 
 * @author Adam_#6723 Handles the preset button interface!
 * 
 */

public class NewPresetsButtonPlugin extends PluginContext {

	/** RuneZerker Inventory! Array **/
	public Item[] Melee126Inventory() {
		return new Item[] { new Item(12695), new Item(3024, 2), new Item(9075, 80), new Item(1215), new Item(3024),
				new Item(6685), new Item(560, 40), new Item(391, 3), new Item(557, 200), new Item(391, 12),
				new Item(3144, 3),

		};
	}

	/** Melee126Equipment **/
	public Item[] Melee126Equipment() {
		return new Item[] { new Item(10828), new Item(1712), new Item(4355), new Item(4587), new Item(1127),
				new Item(8850), new Item(1079), new Item(3105), new Item(2250), new Item(7461) };
	}

	/** Hybrid Inventory! Array **/
	public Item[] Hybrid126Inventory() {
		return new Item[] { new Item(12695), new Item(3024, 2), new Item(9075, 80), new Item(1215), new Item(3024),
				new Item(6685), new Item(560, 40), new Item(391), new Item(391), new Item(391), new Item(557, 200),
				new Item(391, 12), new Item(3144, 4),

		};
	}

	/** Hybrid Equipment Array **/
	public Item[] Hybrid126Equipment() {
		return new Item[] { new Item(10828), new Item(2412), new Item(1712), new Item(4675), new Item(4091),
				new Item(3842), new Item(4093), new Item(3105), new Item(2570), new Item(7461) };
	}

	/** Tribrid Inventory! Array **/
	public Item[] TribridInventory() {
		return new Item[] { new Item(1127), new Item(4587), new Item(2503), new Item(9185), new Item(1079),
				new Item(8850), new Item(2497), new Item(10498), new Item(391), new Item(1215), new Item(6685),
				new Item(2440), new Item(391, 2), new Item(6685), new Item(2436), new Item(391, 2), new Item(3024),
				new Item(2444), new Item(391, 2), new Item(3024), new Item(555, 800), new Item(391, 2),
				new Item(560, 600), new Item(565, 300),

		};
	}

	/**
	 * Handles the dialouges for the presets, to it deducts an X amount from the
	 * vault or the inventory!
	 * 
	 * @param player
	 */

	public void sendMelee126Dialogue(Player player) {

		DialogueFactory factory = player.dialogueFactory;
		factory.sendNpcChat(306, "Would you like this preset for 100k? " + player.getName());
		factory.sendOption("Yes", () -> Melee126(player), "Nevermind", factory::clear);
		factory.execute();

	}

	public void send126HybridDialouge(Player player) {

		DialogueFactory factory = player.dialogueFactory;
		factory.sendNpcChat(306, "Would you like this preset for 100k? " + player.getName());
		factory.sendOption("Yes", () -> tribrid126(player), "Nevermind", factory::clear);
		factory.execute();

	}

	public void send126tribridDialouge(Player player) {

		DialogueFactory factory = player.dialogueFactory;
		factory.sendNpcChat(306, "Would you like this preset for 100k? " + player.getName());
		factory.sendOption("Yes", () -> tribrid126(player), "Nevermind", factory::clear);
		factory.execute();

	}

	/**
	 * Handles the equipping of gear, whilst also subtracting the preset cost from
	 * the players inventory!
	 * 
	 * @param player
	 */
	public void Melee126(Player player) {
		if (player.bank.contains(995, 100000)) {
			player.bank.remove(995, 100000);

			if (!player.equipment.isEmpty() || !player.inventory.isEmpty()) {
				player.bank.depositeInventory();
				player.bank.depositeEquipment();
				player.bank.shift();
				player.bank.refresh();

				player.equipment.manualWearAll(Melee126Equipment());
				player.inventory.addAll(Melee126Inventory());
				player.inventory.refresh();
				player.equipment.refresh();
				player.spellbook = Spellbook.LUNAR;

			}

		} else {
			player.message("You need to have 100k in the bank!");
		}

	}

	public void Hybrid126(Player player) {
		if (player.bank.contains(995, 100000)) {
			player.bank.remove(995, 100000);

			if (!player.equipment.isEmpty() || !player.inventory.isEmpty()) {
				player.bank.depositeInventory();
				player.bank.depositeEquipment();
				player.bank.shift();
				player.bank.refresh();

				player.equipment.manualWearAll(Hybrid126Equipment());
				player.inventory.addAll(Hybrid126Inventory());
				player.inventory.refresh();
				player.equipment.refresh();
				player.spellbook = Spellbook.ANCIENT;

			}

		} else {
			player.message("You need to have 100k in the bank!");
		}

	}

	public void tribrid126(Player player) {
		if (player.bank.contains(995, 100000)) {
			player.bank.remove(995, 100000);

			if (!player.equipment.isEmpty() || !player.inventory.isEmpty()) {
				player.bank.depositeInventory();
				player.bank.depositeEquipment();
				player.bank.shift();
				player.bank.refresh();

				player.equipment.manualWearAll(Hybrid126Equipment());
				player.inventory.addAll(TribridInventory());
				player.inventory.refresh();
				player.equipment.refresh();
				player.spellbook = Spellbook.ANCIENT;

			}

		} else {
			player.message("You need to have 100k in the bank!");
		}

	}

	@Override
	protected boolean onClick(Player player, int button) {
		if (button == -23029) {
			sendMelee126Dialogue(player);
		}

		if (button == -23028) {
			send126HybridDialouge(player);

		}

		if (button == -23027) {
			send126tribridDialouge(player);

		}

		/** Closes the interface! **/
		if (button == -23034) {
			player.interfaceManager.close();
		}

		return false;
	}
}
