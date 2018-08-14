package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.OutgoingPacket;
import com.nardah.net.packet.PacketType;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Sends an input dialogue.
 * @author Daniel
 * @author Michael
 */
public class SendInputAmount extends OutgoingPacket {
	
	private final Consumer<String> action;
	private final String inputMessage;
	private final int inputLength;
	
	public SendInputAmount(Consumer<String> action) {
		this("Enter an amount:", 10, action);
	}
	
	public SendInputAmount(String message, int length, Consumer<String> action) {
		super(27, PacketType.VAR_SHORT);
		this.action = action;
		this.inputMessage = message;
		this.inputLength = length;
	}
	
	@Override
	public boolean encode(Player player) {
		player.enterInputListener = Optional.of(action);
		builder.writeString(inputMessage).writeShort(inputLength, ByteModification.ADD);
		return true;
	}
	
}
