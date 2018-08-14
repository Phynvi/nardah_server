package com.nardah.util.parser.impl;

import com.google.gson.JsonObject;
import com.nardah.net.packet.PacketRepository;
import com.nardah.util.parser.GsonParser;

/**
 * Parses through the packet sizes file and associates their opcode with a size.
 * @author SeVen
 */
public final class PacketSizeParser extends GsonParser {

	/**
	 * Creates a new {@code PacketSizeParser}.
	 */
	public PacketSizeParser() {
		super("def/io/message_sizes", false);
	}

	@Override
	protected void parse(JsonObject data) {
		final int opcode = data.get("opcode").getAsInt();
		final int size = data.get("size").getAsInt();
		PacketRepository.registerType(opcode, size);
	}

}
