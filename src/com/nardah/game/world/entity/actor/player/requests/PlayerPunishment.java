package com.nardah.game.world.entity.actor.player.requests;

import com.nardah.Config;
import com.nardah.Nardah;
import com.nardah.content.activity.impl.JailActivity;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendLogout;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.command.impl.UpdateCommand;
import com.nardah.util.database.query.options.impl.TableColumnValueOption;
import com.nardah.util.database.query.options.impl.WhereConditionOption;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.nardah.util.AccountUtility.GLOBAL_ACCOUNTS_TABLE;

/**
 * Handles the player punishment
 * @author Daniel
 */
public class PlayerPunishment {
	private final Player player;
	
	public long muteStart, muteDuration;
	public long jailStart, jailDuration;
	public long banStart, banDuration;
	
	public PlayerPunishment(Player player) {
		this.player = player;
	}
	
	/**
	 * Muting
	 */
	public void mute(long duration, TimeUnit unit) {
		muteStart = System.currentTimeMillis();
		muteDuration = TimeUnit.MILLISECONDS.convert(duration, unit);
		player.message("<col=F21827>You have been muted for " + duration + " " + unit.name().toLowerCase());
		player.dialogueFactory.sendStatement("You have been muted for " + duration + " " + unit.name().toLowerCase()).execute();
	}
	
	public void unmute() {
		muteStart = -1;
		muteDuration = -1;
	}
	
	public boolean isMuted() {
		return muteStart > 0 && System.currentTimeMillis() - muteStart < muteDuration;
	}
	
	/**
	 * Jailing
	 */
	public void jail(long duration, TimeUnit unit) {
		jailStart = System.currentTimeMillis();
		jailDuration = TimeUnit.MILLISECONDS.convert(duration, unit);
		player.message("<col=F21827>You have been jailed for " + duration + " " + unit.name().toLowerCase());
		player.dialogueFactory.sendStatement("You have been jailed for " + duration + " " + unit.name().toLowerCase()).execute();
		JailActivity.create(player);
	}
	
	public void unJail() {
		jailStart = -1;
		jailDuration = -1;
	}
	
	public boolean isJailed() {
		return jailStart > 0 && System.currentTimeMillis() - jailStart < jailDuration;
	}
	
	public void banUser(long duration, TimeUnit unit) {
		banStart = System.currentTimeMillis();
		banDuration = TimeUnit.MILLISECONDS.convert(duration, unit);
		player.send(new SendLogout());
		World.queueLogout(player);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("BanReasons", "UTF-8");
		} catch(FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("User was banned" + player.getName() + "!");
		writer.println("Reason for their ban:");
		writer.close();

		if (Config.FORUM_INTEGRATION) {
			final SQLCommand command = new UpdateCommand(GLOBAL_ACCOUNTS_TABLE);
			command.addOption(new TableColumnValueOption("ban_duration", banDuration));
			command.addOption(new TableColumnValueOption("ban_start", Instant.now().toEpochMilli()));
			command.addOption(new WhereConditionOption("account_name", player.getUsername()));
			Nardah.getDatabase().execute(command);
		}
	}
	
	public void unBan() {
		banStart = -1;
		banDuration = -1;
	}
	
	public boolean isBanned() {
		return banStart > 0 && System.currentTimeMillis() - banStart < banDuration;
	}
	
}
