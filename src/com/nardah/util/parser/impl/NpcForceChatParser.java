package com.nardah.util.parser.impl;

import com.google.gson.JsonObject;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.util.parser.GsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses through the mob spawn file and creates {@link Mob}s on startup.
 * @author Daniel | Obey
 */
public class NpcForceChatParser extends GsonParser {

	/**
	 * The map containing all the forced messages.
	 */
	public static final Map<Position, ForcedMessage> FORCED_MESSAGES = new HashMap<>();

	/**
	 * Constructs a new <code>NpcForceChatParser</code>.
	 */
	public NpcForceChatParser() {
		super("def/mob/npc_force_chat", false);
	}

	@Override
	protected void parse(JsonObject data) {
		final int id = data.get("id").getAsInt();
		final Position position = builder.fromJson(data.get("position"), Position.class);
		final int interval = data.get("interval").getAsInt();

		final MessageType type = MessageType.valueOf(data.get("type").getAsString());

		String[] messages = new String[]{};

		if(data.has("messages")) {
			messages = builder.fromJson(data.get("messages"), String[].class);
		}

		FORCED_MESSAGES.put(position, new ForcedMessage(id, interval, messages, type));
	}

	/**
	 * The forced message class.
	 */
	public static class ForcedMessage {
		/**
		 * The mob id.
		 */
		private final int id;

		/**
		 * The interval at which the message will be performed.
		 */
		private final int interval;

		/**
		 * The array of messages the mob will perform.
		 */
		private final String[] messages;

		/**
		 * The message type.
		 */
		private final MessageType type;

		/**
		 * The next message.
		 */
		private int next = 0;

		/**
		 * Constructs a new <code>ForcedMessage</code>.
		 * @param id The mob id.
		 * @param interval The interval at which the mob will perform the message.
		 * @param messages The messages the mob will be forced to perform.
		 * @param type The type of message.
		 */
		public ForcedMessage(int id, int interval, String[] messages, MessageType type) {
			this.id = id;
			this.interval = interval;
			this.messages = messages;
			this.type = type;
		}

		public int getId() {
			return id;
		}

		public int getInterval() {
			return interval;
		}

		public String[] getMessages() {
			return messages;
		}

		public MessageType getType() {
			return type;
		}

		public String nextMessage() {
			switch(type) {
				case NORMAL:
					if(next >= messages.length) {
						next = 0;
					}
					return messages[next++];
				case RANDOM:
					return messages[Utility.random(messages.length)];
				default:
					throw new IllegalArgumentException("Unhandled type: " + type + ".");
			}
		}
	}

	/**
	 * The enum of message types.
	 */
	private enum MessageType {
		RANDOM, NORMAL
	}
}
