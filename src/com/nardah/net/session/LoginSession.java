package com.nardah.net.session;

import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.persist.PlayerSerializer;
import com.nardah.util.AccountUtility;
import com.nardah.util.Stopwatch;
import com.nardah.util.Utility;
import com.nardah.Config;
import com.nardah.Nardah;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.game.GamePacketDecoder;
import com.nardah.net.codec.game.GamePacketEncoder;
import com.nardah.net.codec.login.LoginDetailsPacket;
import com.nardah.net.codec.login.LoginResponse;
import com.nardah.net.codec.login.LoginResponsePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a {@link Session} for authenticating users logging in.
 * @author nshusa
 */
public final class LoginSession extends Session {
	
	private static final ConcurrentMap<String, FailedLoginAttempt> failedLogins = new ConcurrentHashMap<>();
	
	public LoginSession(Channel channel) {
		super(channel);
	}
	
	@Override
	public void handleClientPacket(Object o) {
		if(o instanceof LoginDetailsPacket) {
			LoginDetailsPacket packet = (LoginDetailsPacket) o;
			handleUserLoginDetails(packet);
		}
	}
	
	private void handleUserLoginDetails(LoginDetailsPacket packet) {
		final SocketChannel channel = (SocketChannel) packet.getContext().channel();
		
		LoginResponse response = LoginResponse.NO_RESPONSE;
		
		if(failedLogins.containsKey(packet.getUsername())) {
			FailedLoginAttempt failedAttempt = failedLogins.get(packet.getUsername());
			
			if(failedAttempt.getAttempt().get() >= Config.FAILED_LOGIN_ATTEMPTS && !failedAttempt.getStopwatch().elapsed(Config.FAILED_LOGIN_TIMEOUT, TimeUnit.MINUTES)) {
				response = LoginResponse.LOGIN_ATTEMPTS_EXCEEDED;
			} else if(failedAttempt.getAttempt().get() >= Config.FAILED_LOGIN_ATTEMPTS && failedAttempt.getStopwatch().elapsed(Config.FAILED_LOGIN_TIMEOUT, TimeUnit.MINUTES)) {
				failedLogins.remove(packet.getUsername());
			} else {
				failedAttempt.getAttempt().incrementAndGet();
			}
		}
		
		final Player player = new Player(packet.getUsername());
		player.setPassword(packet.getPassword());
		
		if(response == LoginResponse.NO_RESPONSE) {
			response = evaluate(player);
		}
		
		if(response == LoginResponse.INVALID_CREDENTIALS) {
			if(!failedLogins.containsKey(packet.getUsername())) {
				failedLogins.put(packet.getUsername(), new FailedLoginAttempt());
			}
			failedLogins.get(packet.getUsername());
		} else if(response == LoginResponse.NORMAL) {
			failedLogins.remove(packet.getUsername());
		}
		
		final ChannelFuture future = channel.writeAndFlush(new LoginResponsePacket(response, player.right, false));
		
		if(response != LoginResponse.NORMAL) {
			future.addListener(ChannelFutureListener.CLOSE);
			return;
		}
		
		future.awaitUninterruptibly();
		channel.pipeline().replace("login-encoder", "game-encoder", new GamePacketEncoder(packet.getEncryptor()));
		channel.pipeline().replace("login-decoder", "game-decoder", new GamePacketDecoder(packet.getDecryptor()));
		
		final GameSession session = new GameSession(channel, player);
		channel.attr(Config.SESSION_KEY).set(session);
		player.setSession(session);
		
		World.queueLogin(player);
	}
	
	private LoginResponse evaluate(Player player) {
		final String username = player.getUsername();
		final String password = player.getPassword();
		final boolean isEmail = username.contains("@");
		
		// prevents users from logging in before the server is ready to accept
		// connections
		if(!Nardah.serverStarted.get()) {
			return LoginResponse.SERVER_BEING_UPDATED;
		}
		
		//        if (Config.TEST_WORLD && !AccountSecurity.AccountData.forName(username).isPresent()) {
		//            return LoginResponse.INSUFFICIENT_PERMSSION;
		//        }
		
		// the world is currently full
		if(World.getPlayerCount() == Config.MAX_PLAYERS) {
			return LoginResponse.WORLD_FULL;
		}
		
		// prevents users from logging in if the world is being updated
		if(World.update.get()) {
			return LoginResponse.SERVER_BEING_UPDATED;
		}
		if(isEmail) {
			if(!Config.FORUM_INTEGRATION) {
				return LoginResponse.BAD_USERNAME;
			}
			
			if(username.length() > Config.EMAIL_MAX_CHARACTERS || username.length() < Config.EMAIL_MIN_CHARACTERS) {
				return LoginResponse.INVALID_EMAIL;
			}
			
			// does email have illegal characters
			if(!(username.matches("^[a-zA-Z0-9.@]{1," + Config.EMAIL_MAX_CHARACTERS + "}$"))) {
				return LoginResponse.INVALID_CREDENTIALS;
			}
		} else if(username.length() < Config.USERNAME_MIN_CHARACTERS) {
			return LoginResponse.SHORT_USERNAME;
		} else if(username.length() > Config.USERNAME_MAX_CHARACTERS) {
			return LoginResponse.BAD_USERNAME;
		} else if(World.getPlayerByHash(Utility.nameToLong(username)).isPresent()) { // this user is already online
			return LoginResponse.ACCOUNT_ONLINE;
		} else if(!(username.matches("^[a-zA-Z0-9 ]{1," + Config.USERNAME_MAX_CHARACTERS + "}$"))) { // does username
			// have illegal
			// characters
			return LoginResponse.INVALID_CREDENTIALS;
		} else if(password.isEmpty() || password.length() > Config.PASSWORD_MAX_CHARACTERS) {
			return LoginResponse.INVALID_CREDENTIALS;
		}
		
		if(World.search(username).isPresent()) {
			return LoginResponse.ACCOUNT_ONLINE;
		}


		if (Config.FORUM_INTEGRATION) {
			LoginResponse response = LoginResponse.NORMAL;
			if (!AccountUtility.verify(username, password) && !AccountUtility.create(username, password)) {
				response = LoginResponse.INVALID_CREDENTIALS;
			} else {
				response = PlayerSerializer.load(player, password);
			}

			return response;
		}
		
		LoginResponse response = PlayerSerializer.load(player, password);
		
		if(World.searchAll(player.getUsername()).isPresent()) {
			return LoginResponse.ACCOUNT_ONLINE;
		}
		
		return response;
	}
	
	/**
	 * A data class that represents a failed login attempt.
	 * @author nshusa
	 */
	private static class FailedLoginAttempt {
		
		private final AtomicInteger attempt = new AtomicInteger(0);
		private final Stopwatch stopwatch = Stopwatch.start();
		
		public AtomicInteger getAttempt() {
			return attempt;
		}
		
		public Stopwatch getStopwatch() {
			return stopwatch;
		}
		
	}
	
}
