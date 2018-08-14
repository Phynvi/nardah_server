package com.nardah.content.bot.botclass;

import com.nardah.content.bot.PlayerBot;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;

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
