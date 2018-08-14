package io.battlerune.content.dialogue.impl;

import io.battlerune.content.dialogue.Dialogue;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.dialogue.Expression;
import io.battlerune.content.skill.impl.construction.BuildableMap;
import io.battlerune.game.world.entity.actor.player.Player;

public class ConstructionDialogue extends Dialogue {

	private final int id = 5419;

	@Override
	public void sendDialogues(DialogueFactory factory) {
		Player player = factory.getPlayer();

		factory.sendNpcChat(id, Expression.HAPPY, "Hello #name, how may I assist you?").sendOption("I would like to purchase a house (<col=ff0000>1m coins</col>)", () -> {
			factory.onAction(() -> {
				player.house.purchase();
			});

		}, "I want to change my house location", () -> {

			factory.onAction(() -> {
				factory.sendNpcChat(id, Expression.HAPPY, "Be advised, changing home location will result", "in you <col=ff0000>losing</col> all house object.").sendOption("Small cave (100,000gp)", () -> {
					factory.onAction(() -> {
						player.house.location(BuildableMap.SMALL_CAVE);
					});

				}, "Throne room (15,000,000gp)", () -> {
					factory.onAction(() -> {
						player.house.location(BuildableMap.THRONE_ROOM);
					});
				}).execute();
			});

		}, "Nevermind", () -> {
			factory.onAction(() -> {
				player.interfaceManager.close();
			});

		}).execute();
	}
}
