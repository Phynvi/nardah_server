package plugin.click.button;

import com.nardah.content.dialogue.impl.PrestigeDialogue;
import com.nardah.content.prestige.PrestigeData;
import com.nardah.content.store.Store;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.event.impl.NpcClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

public class PrestigeButtonPlugin extends PluginContext {

	@Override
	protected boolean firstClickNpc(Player player, NpcClickEvent event) {
		if (event.getMob().id != 345)
			return false;
		player.dialogueFactory.sendDialogue(new PrestigeDialogue());
		return true;
	}

	@Override
	protected boolean secondClickNpc(Player player, NpcClickEvent event) {
		if (event.getMob().id != 345)
			return false;
		Store.STORES.get("Prestige Rewards Store").open(player);
		return true;
	}

	@Override
	protected boolean thirdClickNpc(Player player, NpcClickEvent event) {
		if (event.getMob().id != 345)
			return false;
		player.prestige.open();
		return true;
	}

	@Override
	protected boolean fourthClickNpc(Player player, NpcClickEvent event) {
		if (event.getMob().id != 345)
			return false;
		player.prestige.perkInformation();
		return true;
	}

	@Override
	protected boolean firstClickItem(Player player, ItemClickEvent event) {
		return player.prestige.activatePerk(event.getItem());
	}

	@Override
	protected boolean onClick(Player player, int button) {
		if (!PrestigeData.forButton(button).isPresent())
			return false;
		if (!player.interfaceManager.isInterfaceOpen(52000))
			return true;
		PrestigeData data = PrestigeData.forButton(button).get();
		if (player.prestige.prestige[data.skill] == 5) {
			player.dialogueFactory.sendNpcChat(345, "You can only prestige a maximum of <col=255>5</col> times!")
					.execute();
			return true;
		}
		if (player.skills.get(data.skill).getMaxLevel() != 99) {
			player.dialogueFactory.sendNpcChat(345, "You can only prestige your " + data.name + " skill when you",
					"have reached level <col=255>99</col>. Your current level is <col=255>"
							+ player.skills.get(data.skill).getMaxLevel() + "</col>.")
					.execute();
			return true;
		}
		if (!player.equipment.isEmpty()) {
			player.dialogueFactory.sendNpcChat(345, "You must withdraw all your equipment before you", "can prestige!")
					.execute();
			return true;
		}
		player.dialogueFactory.sendOption("Prestige <col=255>" + Skill.getName(data.skill) + "</col>",
				() -> player.prestige.prestige(data), "Nevermind", player.interfaceManager::close).execute();
		return true;
	}
}
