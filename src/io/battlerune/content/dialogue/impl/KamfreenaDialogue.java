package io.battlerune.content.dialogue.impl;

import io.battlerune.content.dialogue.Dialogue;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.dialogue.Expression;

/**
 * Handles the kamfreena dialogue.
 * @author Daniel
 */
public class KamfreenaDialogue extends Dialogue {

	@Override
	public void sendDialogues(DialogueFactory factory) {
		factory.sendNpcChat(345, Expression.HAPPY, "Welcome to the warriors guild! You can only enter the", "Cyclops room when you have a minimum of 25 tokens. Once", "inside 25 tokens will be removed every 1 minute 30", "seconds. Defender drop are automatically adjusted.").execute();
	}
}
