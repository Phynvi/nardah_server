package io.battlerune.content.bot.objective;

import io.battlerune.content.bot.PlayerBot;

public interface BotObjectiveListener {

	void init(PlayerBot bot);

	void finish(PlayerBot bot);

}
