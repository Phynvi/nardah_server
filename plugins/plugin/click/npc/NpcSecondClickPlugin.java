package plugin.click.npc;

import io.battlerune.content.dialogue.impl.RealmLordDialogue;
import io.battlerune.content.dialogue.impl.RoyalKingDialogue;
import io.battlerune.content.skill.impl.slayer.SlayerTab;
import io.battlerune.content.store.Store;
import io.battlerune.game.event.impl.NpcClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;

public class NpcSecondClickPlugin extends PluginContext {

	@Override
	protected boolean secondClickNpc(Player player, NpcClickEvent event) {
		final int id = event.getNpc().id;
		switch (id) {
		/* King Royal dialogue */
		case 5523:
			player.dialogueFactory.sendDialogue(new RoyalKingDialogue(2));
			break;

		case 7481:
			Store.STORES.get("Runity Vote Store").open(player);
			break;

		case 1602:
		case 1603:
			Store.STORES.get("Kolodion's Arena Store").open(player);
			break;

		case 506:
		case 507:
		case 513: // falador female
		case 512: // falador male
		case 1032:
			Store.STORES.get("The General Store").open(player);
			break;

		/* Emblem trader. */
		case 315:
			Store.STORES.get("The PvP Store").open(player);
			break;

		case 311:
			if (PlayerRight.isIronman(player) || PlayerRight.isDeveloper(player)) {
				Store.STORES.get("Ironman Store").open(player);
			} else {
				player.message("you cannot access this store, unless you are ironman");

			}
			break;

		/* Nieve */
		case 490:
		case 6797:
			player.slayer.open(SlayerTab.MAIN);
			break;

		/* Realm Lord */
		case 15:
			RealmLordDialogue.enterBattleRealm(player.dialogueFactory);
			break;

		/* Zeke */
		case 527:
			Store.STORES.get("Zeke's Superior Scimitars").open(player);
			break;
		}
		return false;
	}

}
