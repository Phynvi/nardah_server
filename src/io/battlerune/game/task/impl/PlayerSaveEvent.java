package io.battlerune.game.task.impl;

import io.battlerune.content.clanchannel.ClanRepository;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.persist.PlayerSerializer;

public class PlayerSaveEvent extends Task {

	public PlayerSaveEvent() {
		super(150);
	}

	@Override
	public void execute() {
		for(Player player : World.getPlayers()) {
			if(player != null) {
				PlayerSerializer.save(player);
			}
		}
		ClanRepository.saveAllActiveClans();
	}
}
