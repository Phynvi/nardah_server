package io.battlerune.net.codec.game;

import io.battlerune.net.codec.IsaacCipher;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketRepository;
import io.battlerune.net.packet.PacketType;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Optional;

/**
 * The class that reads packets from the client into {@link GamePacket}'s.
 * @author nshusa
 */
public final class GamePacketDecoder extends ByteToMessageDecoder {

	/**
	 * The single logger for this class.
	 */
	private static final Logger logger = LogManager.getLogger(LoggerType.NETWORKING);

	/**
	 * The isaac random used to decrypt a packets opcode.
	 */
	private final IsaacCipher decryptor;

	/**
	 * The current packet opcode.
	 */
	private int opcode;

	/**
	 * The current packet size.
	 */
	private int size;

	/**
	 * The current packet type.
	 */
	private PacketType type = PacketType.EMPTY;

	/**
	 * The current state of this class.
	 */
	private State state = State.OPCODE;

	public GamePacketDecoder(IsaacCipher decryptor) {
		this.decryptor = decryptor;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(state == State.OPCODE) {
			decodeOpcode(in, out);
		} else if(state == State.SIZE) {
			decodeSize(in);
		} else {
			decodePayload(in, out);
		}
	}

	/**
	 * Decodes the packet identifier.
	 * @param in The payload from the client.
	 * @param out The collection of upstream messages.
	 */
	private void decodeOpcode(ByteBuf in, List<Object> out) {
		if(in.isReadable()) {
			opcode = (in.readByte() - decryptor.getKey() & 0xFF);
			type = PacketRepository.lookupType(opcode);
			size = PacketRepository.lookupSize(opcode);
			if(type == PacketType.EMPTY) {
				state = State.OPCODE;
				out.add(new GamePacket(opcode, type, Unpooled.EMPTY_BUFFER));
			} else if(type == PacketType.FIXED) {
				state = State.PAYLOAD;
			} else if(type == PacketType.VAR_BYTE || type == PacketType.VAR_SHORT) {
				state = State.SIZE;
			} else {
				throw new IllegalStateException(String.format("Illegal packet type=%s", type.name()));
			}
		}
	}

	/**
	 * Decodes the packets size.
	 * @param in The payload from the client.
	 */
	private void decodeSize(ByteBuf in) {
		if(in.isReadable()) {
			size = in.readUnsignedByte();
			if(size != 0) {
				state = State.PAYLOAD;
			}
		}
	}

	/**
	 * Decodes the packets payload.
	 * @param in The payload from the client.
	 * @param out The collection of upstream messages.
	 */
	private void decodePayload(ByteBuf in, List<Object> out) {
		if(in.isReadable(size)) {
			final Optional<PacketListener> result = PacketRepository.lookupListener(opcode);

			if(result.isPresent()) {
				final byte[] payload = new byte[size];
				in.readBytes(payload);
				out.add(new GamePacket(opcode, type, Unpooled.wrappedBuffer(payload)));
			} else {
				logger.info(String.format("No listener for client -> server packet=%d", opcode));
			}

			state = State.OPCODE;
		}
	}

	/**
	 * Represents the current state of this class.
	 */
	private enum State {
		OPCODE, SIZE, PAYLOAD
	}

}