package com.nardah.content.quest;

import com.nardah.content.dialogue.Dialogue;
import com.nardah.content.event.impl.*;
import com.nardah.content.writer.InterfaceWriter;
import com.nardah.content.writer.impl.QuestWriter;
import com.nardah.content.event.EventDispatcher;
import com.nardah.content.event.InteractionEvent;
import com.nardah.content.event.InteractionEvent.InteractionType;
import com.nardah.content.event.InteractionEventListener;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendScrollbar;
import com.nardah.net.packet.out.SendString;

/**
 * Handles the execution of a quest.
 * @author Daniel
 */
public abstract class Quest extends Dialogue implements InteractionEventListener {

	/**
	 * The name of the quest.
	 */
	public abstract String name();

	/**
	 * The date of when the quest was written.
	 */
	public abstract String created();

	/**
	 * The amount of points rewarded for completion.
	 */
	public abstract int questPoint();

	/**
	 * The index of the quest.
	 */
	public abstract int index();

	/**
	 * Handles the itemcontainer text.
	 */
	public abstract void update(Player player);

	/**
	 * Handles the onReward for completing the quest.
	 */
	protected abstract void reward(Player player);

	/**
	 * Sets the quest stage for the player.
	 */
	protected void setStage(Player player, int stage) {
		player.quest.stage[index()] = stage;
	}

	/**
	 * Handles refreshing the quest tab.
	 */
	protected void refresh(Player player) {
		InterfaceWriter.write(new QuestWriter(player));
	}

	/**
	 * Cleans up the itemcontainer.
	 */
	protected void clean(Player player) {
		for(int index = 37111; index < 37123; index++) {
			player.send(new SendString("", index));
		}
		player.send(new SendScrollbar(37110, 0));
	}

	/**
	 * Handles completing the quest.
	 */
	protected void complete(Player player) {
		player.quest.setPoints(player.quest.getPoints() + questPoint());
		player.quest.setCompleted(player.quest.getCompleted() + 1);
		player.send(new SendString("You have completed: " + name(), 12144));
		player.send(new SendString("" + player.quest.getPoints(), 12147));
		player.send(new SendMessage("Congratulations, quest completed!"));
		reward(player);
		refresh(player);
		setStage(player, -1);
		player.interfaceManager.open(12140);
	}

	/**
	 * Gets the current quest stage for player.
	 */
	protected int getStage(Player player) {
		return player.quest.stage[index()];
	}

	/**
	 * Gets the current quest state.
	 */
	protected QuestState getState(Player player) {
		int stage = getStage(player);
		return stage == 0 ? QuestState.NOT_STARTED : (stage == -1 ? QuestState.COMPLETED : QuestState.STARTED);
	}

	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		return false;
	}

	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		return false;
	}

	@Override
	public boolean onEvent(Player player, InteractionEvent interactionEvent) {
		final EventDispatcher dispatcher = new EventDispatcher(interactionEvent);
		dispatcher.dispatch(InteractionType.FIRST_CLICK_NPC, e -> clickNpc(player, (FirstNpcClick) e));
		dispatcher.dispatch(InteractionType.SECOND_CLICK_NPC, e -> clickNpc(player, (SecondNpcClick) e));
		dispatcher.dispatch(InteractionType.FIRST_CLICK_OBJECT, e -> clickObject(player, (FirstObjectClick) e));
		dispatcher.dispatch(InteractionType.SECOND_CLICK_OBJECT, e -> clickObject(player, (SecondObjectClick) e));
		return interactionEvent.isHandled();
	}
}
