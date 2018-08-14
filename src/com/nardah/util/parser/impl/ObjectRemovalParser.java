package com.nardah.util.parser.impl;

import com.google.gson.JsonObject;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.region.Region;
import com.nardah.util.parser.GsonParser;

/**
 * Handles parsing the removed object.
 * @author Daniel
 */
public class ObjectRemovalParser extends GsonParser {

	public ObjectRemovalParser() {
		super("def/object/removed_objects", false);
	}

	@Override
	protected void parse(JsonObject data) {
		Position position = builder.fromJson(data.get("position"), Position.class);
		Region.SKIPPED_OBJECTS.add(position);

	}
}