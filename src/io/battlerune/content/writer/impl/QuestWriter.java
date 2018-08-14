package io.battlerune.content.writer.impl;

import io.battlerune.content.quest.Quest;
import io.battlerune.content.quest.QuestManager;
import io.battlerune.content.quest.QuestState;
import io.battlerune.content.writer.InterfaceWriter;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendColor;
import io.battlerune.net.packet.out.SendScrollbar;
import io.battlerune.net.packet.out.SendString;
import io.battlerune.net.packet.out.SendTooltip;

/**
 * Class handles writing on the quest tab itemcontainer.
 * @author Daniel
 */
public class QuestWriter extends InterfaceWriter {

	private final String[] text;

	public QuestWriter(Player player) {
		super(player);
		int total = QuestManager.QUEST_COUNT;
		text = new String[total];

		for(int index = 0; index < total; index++) {
			if(player.quest.get(index).isPresent()) {
				Quest quest = player.quest.get(index).get();
				QuestState state = player.quest.getState(index);
				int color = state == QuestState.NOT_STARTED ? 0xFF0000 : (state == QuestState.COMPLETED ? 0x00FF00 : 0xFFFF00);

				text[index] = quest.name();
				player.send(new SendTooltip("View quest " + quest.name(), index + startingIndex()));
				player.send(new SendColor(index + startingIndex(), color));
			}
		}

		player.send(new SendScrollbar(35450, 0));
		player.send(new SendString("Points: " + player.quest.getPoints(), 35404));
		player.send(new SendString("Completed: " + player.quest.getCompleted() + "/" + total, 35414));
	}

	@Override
	protected int startingIndex() {
		return 35451;
	}

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int[][] color() {
		return null;
	}

	@Override
	protected int[][] font() {
		return null;
	}

}
