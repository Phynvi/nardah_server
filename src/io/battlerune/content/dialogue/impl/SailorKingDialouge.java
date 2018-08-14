package io.battlerune.content.dialogue.impl;

import io.battlerune.content.dialogue.Dialogue;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.dialogue.Expression;
import io.battlerune.content.store.Store;

/**
 * The royal king dialogue.
 * @author Daniel
 */
public class SailorKingDialouge extends Dialogue {

	public SailorKingDialouge(int index) {
	}

	@Override
	public void sendDialogues(DialogueFactory factory) {

		factory.getPlayer();
		factory.sendNpcChat(5608, Expression.HAPPY, "Hello adventurer, how may I help you?");
		factory.sendOption("Open Store", () -> store(factory), "Nevermind", factory::clear);
		factory.execute();
	}

	/*
	 * private void claim(DialogueFactory factory) { //factory.onAction(() ->
	 * DonationService.claimDonation(factory.getPlayer())); }
	 */

	private void store(DialogueFactory factory) {
		factory.sendOption("Open Boss Point Store", () -> Store.STORES.get("Boss Point Store").open(factory.getPlayer()), "Open Skilling Point Store", () -> Store.STORES.get("Skilling Point Store").open(factory.getPlayer()), "Open Trivia Point Store", () -> Store.STORES.get("Trivia Point Store").open(factory.getPlayer()), "Nevermind", factory::clear);
	}
}
