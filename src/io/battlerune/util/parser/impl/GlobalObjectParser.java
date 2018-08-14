package io.battlerune.util.parser.impl;

import com.google.gson.JsonObject;
import io.battlerune.content.activity.impl.battlerealm.BattleRealmObjects;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.object.ObjectDirection;
import io.battlerune.game.world.object.ObjectType;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.parser.GsonParser;

import static io.battlerune.content.activity.impl.battlerealm.BattleRealmObjects.battleRealmObjects;

/**
 * The class that loads all global object on startup.
 * @author Daniel
 */
public class GlobalObjectParser extends GsonParser {

	public GlobalObjectParser() {
		super("def/object/global_objects", false);
	}

	@Override
	protected void parse(JsonObject data) {
		int id = data.get("id").getAsInt();
		ObjectType type = ObjectType.valueOf(data.get("type").getAsInt()).orElseThrow(IllegalArgumentException::new);
		ObjectDirection rotation = ObjectDirection.valueOf(data.get("rotation").getAsString());
		Position position = builder.fromJson(data.get("position"), Position.class);

		CustomGameObject toSpawn = new CustomGameObject(id, position, rotation, type);

		// This is needed because I add them back in the instance later. I need the
		// objects to be interactable in the instance.
		if(Area.inBattleRealmMap(position)) {
			System.out.println("Not adding " + id + " at " + position);
			battleRealmObjects.add(new BattleRealmObjects.ObjectArgs(id, position, rotation, type));
			return; // Hopefully this doesn't break fucking everything
		}

		toSpawn.register();
	}
}
