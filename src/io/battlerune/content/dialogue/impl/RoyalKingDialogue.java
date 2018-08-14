package io.battlerune.content.dialogue.impl;

import io.battlerune.content.dialogue.Dialogue;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.dialogue.Expression;
import io.battlerune.content.store.Store;
import io.battlerune.game.service.DonationService;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendURL;

/**
 * The royal king dialogue.
 * @author Daniel
 */
public class RoyalKingDialogue extends Dialogue {

	private int index;

	public RoyalKingDialogue(int index) {
		this.index = index;
	}

	@Override
	public void sendDialogues(DialogueFactory factory) {
		if(index == 1) {
			claim(factory);
			factory.execute();
			return;
		}
		if(index == 2) {
			store(factory);
			factory.execute();
			return;
		}
		Player player = factory.getPlayer();
		factory.sendNpcChat(5523, Expression.HAPPY, "Hello adventurer, how may I help you?");
		factory.sendOption("Claim Purchase", () -> claim(factory), "Donator Information", () -> player.send(new SendURL("www.runity.io/store")), "Open Store", () -> store(factory), "Nevermind", factory::clear);
		factory.execute();
	}

	private void claim(DialogueFactory factory) {
		factory.onAction(() -> DonationService.claimDonation(factory.getPlayer()));
	}

	private void store(DialogueFactory factory) {
		factory.sendOption("Open Donator Store", () -> Store.STORES.get("Donator Store").open(factory.getPlayer()), "Second Donator Store", () -> Store.STORES.get("Second Donator Store").open(factory.getPlayer()), "Ironman Donator Store", () -> Store.STORES.get("Ironman Donator Store").open(factory.getPlayer()), "Nevermind", factory::clear);
	}
}
