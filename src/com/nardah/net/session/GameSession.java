package com.nardah.net.session;

import com.nardah.game.world.World;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketRepository;
import com.nardah.Config;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a {@link Session} when a {@link Player} has been authenticated and
 * active in the game world.
 * @author nshusa
 */
public final class GameSession extends Session {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.NETWORKING);
	private final Queue<GamePacket> queuedPackets = new ConcurrentLinkedQueue<>();
	private final Queue<GamePacket> outgoingPackets = new ConcurrentLinkedQueue<>();
	
	private final Player player;
	
	GameSession(Channel channel, Player player) {
		super(channel);
		this.player = player;
	}
	
	@Override
	public void handleClientPacket(Object o) {
		if(o instanceof GamePacket) {
			queueClientPacket((GamePacket) o);
		}
	}
	
	@Override
	protected void onClose(ChannelFuture f) {
		World.queueLogout(player);
	}
	
	private void queueClientPacket(final GamePacket packet) {
		if(queuedPackets.size() <= Config.CLIENT_PACKET_THRESHOLD) {
			queuedPackets.offer(packet);
		}
	}
	
	public void processClientPackets() {
		for(int i = 0; i < Config.CLIENT_PACKET_THRESHOLD; i++) {
			try {
				GamePacket packet = queuedPackets.poll();
				if(packet == null) {
					break;
				}
				PacketRepository.sendToListener(player, packet);
			} catch(Exception ex) {
				logger.error("Error processing client packet for " + player);
			}
		}
	}
	
	public void queueServerPacket(GamePacket packet) {
		outgoingPackets.offer(packet);
	}
	
	public void processServerPacketQueue() {
		GamePacket packet;
		while((packet = outgoingPackets.poll()) != null) {
			channel.writeAndFlush(packet);
		}
	}
	
	public void flushPacket(GamePacket packet) {
		if(!channel.isOpen()) {
			return;
		}
		channel.writeAndFlush(packet);
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
