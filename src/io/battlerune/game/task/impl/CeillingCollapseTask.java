package io.battlerune.game.task.impl;

import io.battlerune.game.task.TickableTask;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Area;
import io.battlerune.net.packet.out.SendCameraReset;
import io.battlerune.net.packet.out.SendCameraShake;
import io.battlerune.util.Utility;

public class CeillingCollapseTask extends TickableTask {
	private final Player player;

	public CeillingCollapseTask(Player player) {
		super(false, 6);
		this.player = player;
	}

	@Override
	protected void onSchedule() {
		player.send(new SendCameraShake(3, 2, 3, 2));
	}

	@Override
	protected void onCancel(boolean logout) {
		player.send(new SendCameraReset());
	}

	@Override
	protected void tick() {
		if(!Area.inBarrowsChamber(player)) {
			cancel();
			return;
		}
		player.graphic(60);
		player.speak("Ouch!");
		player.damage(new Hit(Utility.random(5, 8)));
		player.message("Some rocks fall from the ceiling and hit you.");
	}
}
