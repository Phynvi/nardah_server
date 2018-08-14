package plugin.click.npc;

import com.nardah.content.dialogue.impl.RoyalKingDialogue;
import com.nardah.content.skill.impl.slayer.SlayerOfferings;
import com.nardah.game.event.impl.NpcClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class NpcThirdClickPlugin extends PluginContext {

	@Override
	protected boolean thirdClickNpc(Player player, NpcClickEvent event) {
		switch (event.getMob().id) {
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
