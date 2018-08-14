package io.battlerune.content.raids;

import io.battlerune.content.raids.npc.RaidsNpc;
import io.battlerune.content.raids.npc.RaidsNpcData;
import io.battlerune.content.raids.npc.RaidsNpcHandler;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendFadeScreen;

public class RaidsInstance {

	private static Player player;

	public RaidsInstance(Player player) {
		RaidsInstance.player = player;
	}

	public static void onStart() {
		player.send(new SendFadeScreen("Joining raids instance", 0, 0));
		World.schedule(5, () -> {
			join();
		});
	}

	private static void join() {
		processSpawning("npc");
	}

	private static void processSpawning(String type) {
		switch(type) {
			case "npc":
				for(RaidsNpcData data : RaidsNpcData.values()) {
					RaidsNpc npc = RaidsNpcHandler.npcs.get(data);
					if(npc != null) {
						for(int i = 0; i < npc.getNpcs(player).length; i++) {
							npc.getNpcs(player)[i].register();
						}
					}
				}
				break;
		}
	}
}
