package com.nardah.content.quest.impl;

import com.nardah.content.dialogue.Expression;
import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.content.event.impl.NpcInteractionEvent;
import com.nardah.content.quest.Quest;
import com.nardah.content.quest.QuestManager;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendString;

/**
 * Handles the cook's assistant quest.
 * @author Daniel
 */
public class CooksAssistant extends Quest {

	@Override
	public String name() {
		return "Cooks Assistant";
	}

	@Override
	public String created() {
		return "2016/10/23";
	}

	@Override
	public int questPoint() {
		return 1;
	}

	@Override
	public int index() {
		return QuestManager.COOKS_ASSISTANT;
	}

	@Override
	public void update(Player player) {
		clean(player);
		switch(getStage(player)) {
			case 0:
				player.send(new SendString("To start this quest please speak to the Cook located at home", 37116));
				break;
			case 1:
				player.send(new SendString("I have talked to the cook and she needs me to get the following", 37112));
				player.send(new SendString("ingredients to bake a cake for her controlling husband", 37113));
				player.send(new SendString("1x Egg", 37115));
				player.send(new SendString("1x Pot of flour", 37116));
				player.send(new SendString("1x Bucket of milk", 37117));
				break;
			case 2:
				player.send(new SendString("I have given the cook all the items she needed.", 37115));
				player.send(new SendString("I should speak to her again to claim my onReward.", 37116));
				break;
			case 3:
				player.send(new SendString("QUEST COMPLETED", 37111));
				player.send(new SendString("<col=000080>You have completed this quest!", 37112));
				player.send(new SendString("<col=000080>You were awarded:", 37113));
				player.send(new SendString("1 quest point", 37115));
				player.send(new SendString("50,000 cooking experience", 37116));
				player.send(new SendString("100,000 gold coins", 37117));
				break;
		}
	}

	@Override
	public void sendDialogues(DialogueFactory factory) {
		Player player = factory.getPlayer();
		switch(getStage(player)) {
			case 0:
				factory.sendPlayerChat("Hey there...").sendNpcChat(225, Expression.ALMOST_CRYING, "Oh #name, I need your help!", "My husband is coming home soon and I promised him", "I would bake him a cake. Unfortunately, as I am a woman", "I can't leave the kitchen. Can you please help me?").sendOption("Sure", () -> {
					setStage(player, 1);
					player.interfaceManager.close();
					player.dialogueFactory.sendDialogue(this);
				}, "No, I'm too busy right now", () -> {
					factory.sendPlayerChat("No, I'm too busy right now").sendNpcChat(225, Expression.ALMOST_CRYING, "*sigh*", "If you change your mind I will be here.");
				}).execute();
				break;
			case 1:
				Item[] needed = {new Item(1944), new Item(1933), new Item(1927)};
				if(player.inventory.containsAny(needed) && !player.inventory.containsAll(needed)) {
					factory.sendPlayerChat("So what do you need me to do?").sendNpcChat(225, Expression.HAPPY, "I need the following ingredients for the cake:", "1x egg, 1x bucket of milk and 1x pot of flour.").sendPlayerChat("Alright, I will be right back.").execute();
					return;
				}
				if(player.inventory.containsAll(needed)) {
					setStage(player, 2);
					player.inventory.removeAll(needed);
					player.dialogueFactory.sendStatement("You have handed over all your items.").execute();
					return;
				}
				factory.sendPlayerChat("So what do you need me to do?").sendNpcChat(225, Expression.HAPPY, "I need the following ingredients for the cake:", "1x egg, 1x bucket of milk and 1x pot of flour.").sendPlayerChat("Alright, I will be right back.").execute();
				break;
			case 2:
				factory.sendNpcChat(225, Expression.HAPPY, "Thank you so much #name!", "Please take this as a reward").onAction(() -> {
					complete(player);
				}).execute();
				break;
			default:
				factory.sendStatement("You have completed the quest and the", "Cook no longer has anything to say.", "Yes, the cook used you.").execute();
				break;
		}
	}

	@Override
	public void reward(Player player) {
		player.skills.addExperience(Skill.COOKING, 50000, false);
		player.inventory.add(new Item(20527, 100_000), -1, true);
		player.send(new SendString("50,000 cooking experience", 12150));
		player.send(new SendString("100,000 Royale tokens", 12151));
		player.send(new SendString("1 Quest point", 12152));
		player.send(new SendString("", 12153));
		player.send(new SendString("", 12154));
		player.send(new SendString("", 12155));
	}

	@Override
	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		if(event.getOpcode() == 0 && event.getMob().id == 225) {
			player.dialogueFactory.sendDialogue(this);
			return true;
		}
		return false;
	}
}
