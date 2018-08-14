package io.battlerune.content.writer.impl;

import io.battlerune.content.writer.InterfaceWriter;
import io.battlerune.game.world.entity.mob.player.Player;

/**
 * Class handles writing on the server settings itemcontainer.
 * @author Daniel
 */
public class SettingWriter extends InterfaceWriter {
	private String[] text = {"", "</col>Welcome screen: " + format(player.settings.welcomeScreen), "</col>TriviaBot: " + format(player.settings.triviaBot), "</col>Global yell: " + format(player.settings.yell), "</col>Drop notification: " + format(player.settings.dropNotification), "</col>Untradeables notification: " + format(player.settings.untradeableNotification), "</col>Prestige colors: " + format(player.settings.prestigeColors), "",};

	public SettingWriter(Player player) {
		super(player);
	}

	private String format(boolean parameter) {
		return parameter ? "<col=47781F>Enabled" : "<col=F02E2E>Disabled";
	}

	@Override
	protected int startingIndex() {
		return 51011;
	}

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int[][] color() {
		return null;
	}

	@Override
	protected int[][] font() {
		return null;
	}
}
