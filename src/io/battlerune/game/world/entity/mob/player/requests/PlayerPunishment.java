package io.battlerune.game.world.entity.mob.player.requests;

import io.battlerune.content.activity.impl.JailActivity;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendLogout;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

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
	}
	
	public void unBan() {
		banStart = -1;
		banDuration = -1;
	}
	
	public boolean isBanned() {
		return banStart > 0 && System.currentTimeMillis() - banStart < banDuration;
	}
	
}
