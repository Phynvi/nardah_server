package io.battlerune.game.event.email;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendInputMessage;
import io.battlerune.net.packet.out.SendMessage;

import java.sql.SQLException;

public class EmailInputListener {

	public static void input(Player player) {
		player.locking.lock();
		player.dialogueFactory.sendStatement("For security purposes we would like u to put in your email!.").onAction(() -> {
			player.send(new SendInputMessage("Enter the email:", 20, input -> {
				try {
					SqlEmailListener listener = new SqlEmailListener(player.getUsername(), input);
					listener.connect();
					player.send(new SendMessage("Thank you for securing your account"));
				} catch(ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}));
		}).execute();
	}

}
