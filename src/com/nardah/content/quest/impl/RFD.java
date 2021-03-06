package com.nardah.content.quest.impl;

import com.nardah.content.activity.impl.recipefordisaster.RecipeForDisaster;
import com.nardah.content.store.impl.RecipeForDisasterStore;
import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.content.event.impl.NpcInteractionEvent;
import com.nardah.content.event.impl.ObjectInteractionEvent;
import com.nardah.content.quest.Quest;
import com.nardah.content.quest.QuestManager;
import com.nardah.content.quest.QuestState;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendFadeScreen;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendString;

/**
 * Handles the rfd quest.
 * @author Daniel
 */
public class RFD extends Quest {

	@Override
	public String name() {
		return "Recipe For Disaster";
	}

	@Override
	public String created() {
		return "2017/09/16";
	}

	@Override
	public int questPoint() {
		return 1;
	}

	@Override
	public int index() {
		return QuestManager.RFD;
	}

	@Override
	public void update(Player player) {
		clean(player);
		switch(getStage(player)) {
			case 0:
				player.send(new SendString("To start this quest please speak to Sbott located at home", 37116));
				break;
			case 1:
				player.send(new SendString("I have talked to Sbott. He wants me to complete the ", 37112));
				player.send(new SendString("Recipe for disaster activity. I can do this by clicking", 37113));
				player.send(new SendString("on the portal next to him. I should have a high combat level", 37115));
				player.send(new SendString("with good gear and weapons. Ice gloves are recommended.", 37116));
				break;
			default:
				player.send(new SendString("I have completed this quest.", 37112));
		}
	}

	@Override
	public void sendDialogues(DialogueFactory factory) {
		Player player = factory.getPlayer();
		if(getState(player) == QuestState.COMPLETED) {
			factory.sendStatement("You have already completed this quest!").execute();
			return;
		}

		switch(getStage(player)) {
			case 0:
				factory.sendNpcChat(6526, "Greetings adventurer.", "Would you like to partake in my activity?", "By completing it you will unlock barrows gloves.").sendOption("Sure", () -> {
					setStage(player, 1);
					factory.clear();
				}, "No, I'm too busy right now", () -> factory.sendPlayerChat("No, I'm too busy right now").execute()).execute();
				break;
			case 1:
				if(player.glovesTier >= 9) {
					complete(player);
					return;
				}
				factory.sendNpcChat(6526, "Click on the portal next to me to enter the activity.", "It is safe, items will not be lost on death.", "In order to retrieve the highest tier gloves you need", "to complete the activity in one inventory").execute();
				break;
		}
	}

	@Override
	public void reward(Player player) {
		player.send(new SendString("Barrows Gloves", 12150));
		player.send(new SendString("1 Quest point", 12151));
		player.send(new SendString("", 12152));
		player.send(new SendString("", 12153));
		player.send(new SendString("", 12154));
		player.send(new SendString("", 12155));
	}

	@Override
	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		if(event.getOpcode() == 0 && event.getMob().id == 6526) {
			player.dialogueFactory.sendDialogue(this);
			return true;
		}
		if(event.getOpcode() == 1 && event.getMob().id == 6526) {
			if(player.glovesTier == 0) {
				player.dialogueFactory.sendDialogue(this);
				return true;
			}
			new RecipeForDisasterStore().open(player);
			return true;
		}
		return false;
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		if(event.getOpcode() == 0 && event.getObject().getId() == 4150) {
			if(getState(player) == QuestState.NOT_STARTED) {
				player.send(new SendMessage("Please speak to Sbott before doing this!"));
				return true;
			}
			if(getState(player) == QuestState.STARTED) {
				if(player.glovesTier >= 9) {
					player.dialogueFactory.sendStatement("You have already completed this activity. Therefore you can not enter!").execute();
					return true;
				}
				player.locking.lock();
				player.send(new SendFadeScreen("Entering the Recipe For Disaster activity...", 1, 3));
				World.schedule(5, () -> {
					RecipeForDisaster.create(player);
					player.locking.unlock();
				});
				return true;
			}
			if(getState(player) == QuestState.COMPLETED) {
				player.dialogueFactory.sendStatement("You have already completed this activity.", "Would you like to attempt again for fun?").sendOption("Yes", () -> {
					player.locking.lock();
					player.send(new SendFadeScreen("Entering the Recipe For Disaster activity...", 1, 3));
					World.schedule(5, () -> {
						RecipeForDisaster.create(player);
						player.locking.unlock();
					});
				}, "No", () -> player.dialogueFactory.clear()).execute();
				return true;
			}
			return true;
		}
		return false;
	}
}
