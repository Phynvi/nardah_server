package com.nardah.content.writer.impl;

import com.nardah.content.quest.Quest;
import com.nardah.content.quest.QuestManager;
import com.nardah.content.quest.QuestState;
import com.nardah.content.writer.InterfaceWriter;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendColor;
import com.nardah.net.packet.out.SendScrollbar;
import com.nardah.net.packet.out.SendString;
import com.nardah.net.packet.out.SendTooltip;

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
