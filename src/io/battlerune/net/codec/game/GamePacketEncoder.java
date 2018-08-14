package io.battlerune.net.codec.game;

import io.battlerune.net.codec.IsaacCipher;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class GamePacketEncoder extends MessageToByteEncoder<GamePacket> {

	private final IsaacCipher encryptor;

	public GamePacketEncoder(IsaacCipher encryptor) {
		this.encryptor = encryptor;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, GamePacket packet, ByteBuf out) throws Exception {
		out.writeByte((packet.getOpcode() + encryptor.getKey()) & 0xFF);
		if(packet.getHeader() == PacketType.VAR_BYTE) {
			out.writeByte(packet.getSize());
		} else if(packet.getHeader() == PacketType.VAR_SHORT) {
			out.writeShort(packet.getSize());
		}
		out.writeBytes(packet.getPayload());
	}

}
