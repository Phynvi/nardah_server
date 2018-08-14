package com.nardah.util.parser.impl;

import com.google.gson.JsonObject;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.position.Position;
import com.nardah.util.parser.GsonParser;

/**
 * Parses through the mob spawn file and creates {@link Mob}s on startup.
 * @author Daniel | Obey
 */
public class NpcSpawnParser extends GsonParser {

	public NpcSpawnParser() {
		super("def/mob/npc_spawns");
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
		new Mob(id, position, radius, facing).register();
	}

}
