package com.nardah.game.task.impl;

import com.nardah.content.clanchannel.ClanRepository;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.persist.PlayerSerializer;
import com.nardah.game.task.Task;
import com.nardah.game.world.entity.actor.player.Player;

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
