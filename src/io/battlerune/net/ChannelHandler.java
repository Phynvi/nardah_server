package io.battlerune.net;

import com.sun.org.apache.bcel.internal.generic.NEW;
import io.battlerune.Config;
import io.battlerune.net.session.Session;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Optional;

/**
 * A {@link SimpleChannelInboundHandler} implementation for re-routing
 * channel-events to its bound {@link Session}.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
@Sharable
public final class ChannelHandler extends SimpleChannelInboundHandler<Object> {
	
	/**
	 * Logger.
	 */
	private static final Logger logger = LogManager.getLogger(LoggerType.NETWORKING);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
		try {
			Optional<Session> result = Optional.ofNullable(ctx.channel().attr(Config.SESSION_KEY).get());
			result.ifPresent(it -> it.handleClientPacket(o));
		} catch(Exception ex) {
			logger.error("Error reading channel!");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Optional.ofNullable(ctx.channel().attr(Config.SESSION_KEY).get()).ifPresent(Session::close);
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event.state() == IdleState.READER_IDLE) {
				ctx.channel().close();
			}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		if(Config.IGNORED_EXCEPTIONS.stream().noneMatch(e.getMessage()::equals)) {
			logger.error("Exception caught upstream!");
			e.printStackTrace();
		}
		
		Optional.ofNullable(ctx.channel().attr(Config.SESSION_KEY).get()).ifPresent(Session::close);
	}
	
}
