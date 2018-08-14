package plugin.click.object;

import io.battlerune.Config;
import io.battlerune.content.Obelisks;
import io.battlerune.content.WellOfGoodwill;
import io.battlerune.content.combat.cannon.CannonManager;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.skill.impl.magic.Spellbook;
import io.battlerune.content.store.impl.PersonalStore;
import io.battlerune.game.Animation;
import io.battlerune.game.action.impl.FlaxPickingAction;
import io.battlerune.game.event.impl.ObjectClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.magic.Autocast;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.net.packet.out.SendMessage;

public class ObjectSecondClickPlugin extends PluginContext {

	@Override
	protected boolean secondClickObject(Player player, ObjectClickEvent event) {
		final GameObject object = event.getObject();
		final int id = object.getId();
		switch (id) {

		case 26760:
			player.message("There are " + World.get().getWildernessResourcePlayers()
					+ " players in the wilderness resource area.");
			break;

		case 14826:
		case 14827:
		case 14828:
		case 14829:
		case 14830:
		case 14831:
			if (player.isTeleblocked()) {
				player.message("You cannot use the obelisk while teleblocked.");
			} else {
				Obelisks.get().open(player, id);
				player.message("you have teleported using the obelisk.");
			}
			break;

		case 29150:
			Autocast.reset(player);
			player.animate(new Animation(645));
			player.spellbook = Spellbook.ANCIENT;
			player.interfaceManager.setSidebar(Config.MAGIC_TAB, player.spellbook.getInterfaceId());
			player.send(
					new SendMessage("You are now using the " + player.spellbook.name().toLowerCase() + " spellbook."));
			break;

		/* Well */
		case 884:
			WellOfGoodwill.open(player);
			break;

		/* Dwarf cannon. */
		case 6:
			CannonManager.pickup(player);
			break;

		/* Flax picking. */
		case 14896:
			if (player.inventory.remaining() == 0) {
				player.dialogueFactory.sendStatement("You do not have enough inventory spaces to do this!").execute();
				break;
			}
			player.action.execute(new FlaxPickingAction(player, object), true);
			break;

		/* Grand exchange. */
		case 26044: {
			if (PlayerRight.isIronman(player)) {
				player.send(new SendMessage("As an iron man you may not access player owned stores!"));
				break;
			}
			DialogueFactory f = player.dialogueFactory;
			f.sendOption("Browse all stores", () -> f.onAction(() -> {
				PersonalStore.openPanel(player);
			}), "Open my shop", () -> f.onAction(() -> {
				PersonalStore.myShop(player);
			}), "Edit my shop", () -> f.onAction(() -> {
				PersonalStore.edit(player);
			}), "Collect coins", () -> f.onAction(() -> {
				// TODO
			})).execute();
		}
			break;

		default:
			return false;

		}
		return true;
	}

}
