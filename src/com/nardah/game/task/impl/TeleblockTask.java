package com.nardah.game.task.impl;

import com.nardah.game.task.Task;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendWidget;

/**
 * This randomevent handles the teleblock counter.
 * @author Daniel | Obey
 */
public class TeleblockTask extends Task {
	
	private final Player player;
	
	public TeleblockTask(Player player) {
		super(false, 0);
		this.player = player;
	}
	
	@Override
	protected void execute() {
		if(player == null || !player.isValid()) {
			cancel();
			return;
		}
		
		if(player.teleblockTimer.decrementAndGet() <= 0) {
			cancel();
		}
	}
	
	@Override
	protected void onCancel(boolean logout) {
		player.send(new SendWidget(SendWidget.WidgetType.TELEBLOCK, 0));
		player.send(new SendMessage("You feel the effects of the tele-block spell go away."));
	}
	
}