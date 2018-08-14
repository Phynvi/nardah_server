package io.battlerune.game.service;

import io.battlerune.Nardah;
import io.battlerune.net.ServerPipelineInitializer;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;

import java.util.concurrent.TimeUnit;

/**
 * The bootstrap that will prepare the game and net.
 * @author Seven
 */
public final class NetworkService {
	
	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(LoggerType.NETWORKING);
	
	public void start(int port) throws Exception {
		logger.info("Starting network service on port: " + port);
		
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ServerPipelineInitializer());
			
			ChannelFuture f = b.bind(port).syncUninterruptibly();
			
			Nardah.serverStarted.set(true);
			
			logger.info(String.format("Server built successfully (took %d seconds).", Nardah.UPTIME.elapsedTime(TimeUnit.SECONDS)));
			Nardah.UPTIME.reset();
			f.channel().closeFuture().sync();
		} catch(Exception ex) {
			logger.error("Error starting network service.");
			ex.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
}