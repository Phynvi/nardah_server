package io.battlerune.util.parser.impl;

import com.google.gson.JsonObject;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.parser.GsonParser;

/**
 * Parses through the npc spawn file and creates {@link Npc}s on startup.
 * @author Daniel | Obey
 */
public class NpcSpawnParser extends GsonParser {

	public NpcSpawnParser() {
		super("def/npc/npc_spawns");
	}

	@Override
	protected void parse(JsonObject data) {
		final int id = data.get("id").getAsInt();
		final Position position = builder.fromJson(data.get("position"), Position.class);
		Direction facing;
		try {
			facing = Direction.valueOf(data.get("facing").getAsString());
		} catch(NullPointerException e) {
			facing = Direction.valueOf("NORTH");
		}
		int radius = 2;
		if(data.has("radius")) {
			radius = data.get("radius").getAsInt();
		}
		new Npc(id, position, radius, facing).register();
	}

}
