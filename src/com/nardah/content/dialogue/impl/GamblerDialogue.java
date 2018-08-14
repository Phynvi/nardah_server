package com.nardah.content.dialogue.impl;

import com.nardah.content.dialogue.Dialogue;
import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.content.dialogue.Expression;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendInputAmount;

import java.util.Random;

/**
 * Handles the clan master dialogue.
 * @author Red
 */
public class GamblerDialogue extends Dialogue {

	private Mob mob;

	public GamblerDialogue(Mob mob) {
		this.mob = mob;
	}

	@Override
	public void sendDialogues(DialogueFactory factory) {
		Player player = factory.getPlayer();
		factory.sendNpcChat(1012, Expression.HAPPY, "Welcome! Use an item on me to make a", "bet, or enter how many coins you would like to wager!");
		factory.onAction(() -> World.schedule(1, () -> player.send(new SendInputAmount("How much would you like to bet?", 10, input -> bet(factory, Integer.parseInt(input))))));
		factory.execute();
	}

	private void bet(DialogueFactory factory, int i) {
		factory.clear();
		Player player = factory.getPlayer();

		if(!player.inventory.contains(995, i)) {
			factory.sendNpcChat(1012, Expression.LAUGH, "You do not have enough!");
			factory.execute();
			return;
		}

		int roll = new Random().nextInt(100);
		mob.speak("You rolled a " + roll);
		if(roll >= 55) {
			player.inventory.add(995, i);
		} else {
			player.inventory.remove(995, i);
		}
		// Somehow send something that tells player what they rolled

	}
}
