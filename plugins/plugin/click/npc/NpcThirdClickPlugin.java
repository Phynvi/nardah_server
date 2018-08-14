package plugin.click.npc;

import io.battlerune.content.dialogue.impl.RoyalKingDialogue;
import io.battlerune.content.skill.impl.slayer.SlayerOfferings;
import io.battlerune.game.event.impl.NpcClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.mob.player.Player;

public class NpcThirdClickPlugin extends PluginContext {

	@Override
	protected boolean thirdClickNpc(Player player, NpcClickEvent event) {
		switch (event.getNpc().id) {
		case 5523:
			player.dialogueFactory.sendDialogue(new RoyalKingDialogue(1));
			break;
		case 490:
			SlayerOfferings.offer(player);
			break;
		case 311:
			player.playerAssistant.claimIronmanArmour();
			break;
		}
		return false;
	}

}
