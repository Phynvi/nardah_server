package com.nardah.content.bot.objective;

import com.nardah.content.bot.PlayerBot;

public interface BotObjectiveListener {

	void init(PlayerBot bot);

	void finish(PlayerBot bot);

}
