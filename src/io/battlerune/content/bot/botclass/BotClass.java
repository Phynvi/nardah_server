package io.battlerune.content.bot.botclass;

import io.battlerune.content.bot.PlayerBot;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;

public interface BotClass {

	Item[] inventory();

	Item[] equipment();

	int[] skills();

	void initCombat(Player target, PlayerBot bot);

	void handleCombat(Player target, PlayerBot bot);

	void endFight(PlayerBot bot);

	void pot(Player target, PlayerBot bot);

	void eat(Player target, PlayerBot bot);

}
