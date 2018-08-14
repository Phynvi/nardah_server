package com.nardah.content.raids;

import com.nardah.content.raids.npc.RaidsNpc;
import com.nardah.content.raids.npc.RaidsNpcData;
import com.nardah.content.raids.npc.RaidsNpcHandler;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendFadeScreen;

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
		processSpawning("mob");
	}

	private static void processSpawning(String type) {
		switch(type) {
			case "mob":
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
