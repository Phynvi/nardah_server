package com.nardah.content;

import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Handles the yelling command.
 * @author Daniel
 */
public class Yell {
	
	/**
	 * Array of all invalid strings.
	 */
	private static final String[] INVALID = {".com", "@cr", "<img=", "</col", "<col=", "@whi@", "@blu@", "@gre@", "@red@", "@mag@", "@cya@"};
	
	/**
	 * Yells a message to the server.
	 */
	public static void yell(Player player, String message) {
		if(!PlayerRight.isDonator(player) || !PlayerRight.isSupreme(player) || !PlayerRight.isManagement(player)) {
			player.send(new SendMessage("You must be a donator to use this command!"));
			return;
		}
		
		if(!player.settings.yell) {
			player.send(new SendMessage("You can not send a yell message as you have the yell setting disabled!"));
			return;
		}
		
		if(player.punishment.isMuted()) {
			player.message("You are muted and can not yell!");
			return;
		}
		
		if(player.punishment.isJailed()) {
			player.message("You are jailed and can not yell!");
			return;
		}
		
		if(!player.yellDelay.elapsed(20, TimeUnit.SECONDS) && !PlayerRight.isManagement(player)) {
			player.message("You can only yell every 20 seconds!");
			return;
		}
		
		if(Arrays.stream(INVALID).anyMatch(message::contains)) {
			player.send(new SendMessage("Your message contains invalid characters."));
			return;
		}
		
		final String prefix = "[<col=" + player.right.getColor() + ">" + player.right.getName() + "</col>] <col=" + player.right.getColor() + ">" + player.getName();
		final String formatted_message = prefix + "</col>: " + Utility.capitalizeSentence(message);
		World.sendMessage(formatted_message, exception -> exception.settings.yell);
		player.yellDelay.reset();
	}
}
