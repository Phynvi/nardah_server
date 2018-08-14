package plugin.click.button;

import static io.battlerune.content.quest.QuestManager.QUEST_COUNT;

import io.battlerune.content.quest.QuestManager;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendString;

public class QuestButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		int base_button = -30085;
		int modified_button = (base_button - button);
		int index = Math.abs(modified_button);
		if (index >= QUEST_COUNT)
			return false;
		for (int i = 0; i < 10; i++) {
			player.send(new SendString("", 37111 + i));
		}
		QuestManager.QUESTS[index].update(player);
		player.send(new SendString("Written:\\n" + QuestManager.QUESTS[index].created(), 37107));
		player.send(new SendString(QuestManager.QUESTS[index].name(), 37103));
		player.interfaceManager.open(37100);
		return true;
	}
}
