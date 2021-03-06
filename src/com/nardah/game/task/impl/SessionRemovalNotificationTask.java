package com.nardah.game.task.impl;

import com.nardah.game.world.entity.actor.player.exchange.ExchangeSession;
import com.nardah.game.task.Task;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendString;

public class SessionRemovalNotificationTask extends Task {

	private final Player player;
	private int time = 11;

	public SessionRemovalNotificationTask(Player player) {
		super(true, 0);
		this.player = player;
	}

	@Override
	public void execute() {
		if(!ExchangeSession.getSession(player).isPresent()) {
			cancel();
			return;
		}

		if(time <= 0) {
			cancel();
			return;
		}

		time--;

		player.send(new SendString(time % 2 == 0 ? "<col=ff0000>Trade has been modified!" : "", 33030));
	}

	@Override
	protected void onCancel(boolean logout) {
		player.send(new SendString("", 33030));
	}

}
