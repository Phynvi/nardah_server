package com.nardah.game.world;

import com.nardah.content.activity.impl.battlerealm.BattleRealm;
import com.nardah.content.activity.impl.pestcontrol.PestControl;
import com.nardah.content.activity.record.GlobalRecords;
import com.nardah.content.bot.PlayerBot;
import com.nardah.content.clanchannel.ClanRepository;
import com.nardah.game.world.entity.MobList;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.MobAssistant;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.actor.player.persist.PlayerSerializer;
import com.nardah.game.world.object.GameObject;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.region.RegionManager;
import com.nardah.net.packet.out.*;
import com.nardah.util.GameSaver;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.Config;
import com.nardah.game.Graphic;
import com.nardah.game.Projectile;
import com.nardah.game.event.bus.DataBus;
import com.nardah.game.event.listener.WorldEventListener;
import com.nardah.game.task.Task;
import com.nardah.game.task.TaskManager;
import com.nardah.game.task.impl.PlayerRemovalTask;
import com.nardah.game.task.impl.SystemUpdateEvent;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.region.Region;
import io.battlerune.net.packet.out.*;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * Represents the game world.
 * @author Daniel
 * @author Michael
 * @author Artem
 */
public final class World {
	
	/**
	 * World {@link Logger}.
	 */
	private static final Logger logger = LogManager.getLogger(LoggerType.SYSTEM);
	
	/**
	 * The players registered in this world.
	 */
	public final MobList<Player> players = new MobList<>(Config.MAX_PLAYERS);
	
	/**
	 * The mobs registered in this world.
	 */
	private final MobList<Mob> npcs = new MobList<>(Config.MAX_NPCS);
	
	/**
	 * The {@link Player}s waiting to login.
	 */
	private final Queue<Player> logins = new ConcurrentLinkedQueue<>();
	
	/**
	 * The {@link Player}s waiting to onLogout.
	 */
	private final Queue<Player> logouts = new ConcurrentLinkedQueue<>();
	
	/**
	 * The task manager.
	 */
	private final TaskManager taskManager = new TaskManager();
	
	/**
	 * The region manager.
	 */
	private final RegionManager regionManager = new RegionManager();
	
	/**
	 * Update flag boolean.
	 */
	public static final AtomicBoolean update = new AtomicBoolean(false);
	
	/**
	 * {@link DataBus} instance.
	 */
	private static final DataBus dataBus = DataBus.getInstance();
	
	/**
	 * The world instance.
	 */
	private static final World WORLD = new World();
	
	/**
	 * Private constructor of {@link World}.
	 */
	private World() {
		dataBus.subscribe(new WorldEventListener());
	}
	
	/**
	 * Handles the world sequencing.
	 */
	public void sequence() {
	
	}
	
	/**
	 * Saves all the game data.
	 */
	public static void save() {
		System.out.println("Saving Nardah...");
		get().players.forEach(PlayerSerializer::save);
		logger.info("All players were successfully saved.");
		GlobalRecords.save();
		logger.info("All global records were successfully saved.");
		ClanRepository.saveAllActiveClans();
		logger.info("All clans were successfully saved.");
		GameSaver.save();
		logger.info("All game data were successfully saved.");
	}
	
	/**
	 * Updates the server.
	 */
	public static void update(int time, boolean restart) {
		if(!update.get()) {
			update.set(true);
			schedule(new SystemUpdateEvent(time * 60, restart));
			get().players.stream().forEach(it -> it.send(new SendGameMessage(0, time, "System " + (restart ? "restart" : "update") + " in:")));
		}
	}
	
	/**
	 * Shuts down the server.
	 */
	public static void shutdown() {
		System.out.println("shutting down...");
		save();
		System.exit(0);
	}
	
	/**
	 * Restarts the server.
	 */
	public static void restart() {
		System.out.println("restarting server...");
		save();
		try {
			Runtime.getRuntime().exec("cmd /c start Run.bat");
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * Handles queueing the player logins.
	 */
	public static void queueLogin(Player player) {
		if(player.isBot) {
			get().logins.add(player);
		} else {
			player.getSession().ifPresent(it -> World.get().logins.add(player));
		}
	}
	
	/**
	 * Handles queueing the player logouts.
	 */
	public static void queueLogout(Player player) {
		if(player == null || get().logouts.contains(player)) {
			return;
		}
		
		get().logouts.add(player);
	}
	
	/**
	 * Gets a player by name.
	 */
	public static Optional<Player> search(String name) {
		for(Player player : get().players) {
			if(player == null) {
				continue;
			}
			
			if(player.getUsername().equalsIgnoreCase(name)) {
				return Optional.of(player);
			}
		}
		return Optional.empty();
	}
	
	public static Optional<Player> searchAll(String name) {
		for(Player player : get().players) {
			if(player == null) {
				continue;
			}
			
			if(player.getUsername().equalsIgnoreCase(name)) {
				return Optional.of(player);
			}
		}
		
		for(Player player : World.get().getLogins()) {
			if(player == null) {
				continue;
			}
			
			if(player.getUsername().equalsIgnoreCase(name)) {
				return Optional.of(player);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Returns a player within an optional whose name hash is equal to {@code
	 * username}.
	 */
	public static Optional<Player> getPlayerByHash(long usernameHash) {
		return get().players.findFirst(it -> Objects.equals(it.usernameLong, usernameHash));
	}
	
	/**
	 * Gets a player by index.
	 */
	public static Optional<Player> getPlayerBySlot(int index) {
		return get().players.stream().filter($it -> $it.getIndex() == index).findFirst();
	}
	
	/**
	 * Gets an mob by index.
	 */
	public static Optional<Mob> getNpcBySlot(int index) {
		return get().npcs.stream().filter($it -> $it.getIndex() == index).findFirst();
	}
	
	/**
	 * Kicks all valid players that meet a certain condition
	 */
	public static void kickPlayer(String name) {
		search(name).ifPresent(it -> it.logout(true));
	}
	
	public static void kickPlayer(Predicate<Player> condition) {
		for(Player player : get().players) {
			if(player.isBot)
				continue;
			if(!condition.test(player))
				continue;
			player.send(new SendLogout());
			queueLogout(player);
		}
	}
	
	public void process() {
		synchronized(World.class) {
			try {
				taskManager.processTasks();
				PestControl.sequenceMinigame();
				BattleRealm.sequenceMinigame();
			} catch(Exception ex) {
				logger.error("Error sequencing pest control or battlerealm");
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Submits a new event.
	 */
	public static void schedule(Task task) {
		get().taskManager.schedule(task);
	}
	
	/**
	 * Executes a runnable on a delay.
	 */
	public static void schedule(int delay, Runnable runnable) {
		schedule(new Task(delay) {
			@Override
			public void execute() {
				runnable.run();
				cancel();
			}
		});
	}
	
	/**
	 * Sends a graphic to the world.
	 */
	public static void sendGraphic(Graphic graphic, Position position, int instance) {
		for(Player player : getPlayers()) {
			if(player.instance != instance)
				continue;
			if(!Utility.withinDistance(player, position, Region.VIEW_DISTANCE))
				continue;
			player.send(new SendGraphic(graphic, position));
		}
	}
	
	/**
	 * Sends a graphic to the world.
	 */
	public static void sendGraphic(Graphic graphic, Position position) {
		get().players.stream().filter(player -> Utility.withinDistance(player, position, Region.VIEW_DISTANCE)).forEach(player -> {
			player.send(new SendGraphic(graphic, position));
		});
	}
	
	/**
	 * Sends a world object animation.
	 */
	public static void sendObjectAnimation(int animation, GameObject object) {
		get().players.stream().filter($it -> Utility.withinDistance(object, $it, Region.VIEW_DISTANCE)).forEach($it -> {
			$it.send(new SendObjectAnimation(animation, object));
		});
	}
	
	/**
	 * Sends a world projectile.
	 */
	public static void sendProjectile(Projectile projectile, Position position, int lock, byte offsetX, byte offsetY) {
		get().players.stream().filter($it -> Utility.withinDistance($it, position, Region.VIEW_DISTANCE)).forEach($it -> $it.send(new SendProjectile(projectile, position, lock, offsetX, offsetY)));
	}
	
	/**
	 * Sends a world projectile.
	 */
	public static void sendProjectile(Actor source, Actor target, Projectile projectile) {
		int lockon = target.isNpc() ? target.getIndex() + 1 : -target.getIndex() - 1;
		int sourceX = source.getX() + source.width() / 2, sourceY = source.getY() + source.length() / 2;
		int targetX = target.getX() + target.width() / 2, targetY = target.getY() + target.length() / 2;
		byte offsetX = (byte) (targetX - sourceX);
		byte offsetY = (byte) (targetY - sourceY);
		Position center = new Position(sourceX, sourceY);
		get().players.stream().filter($it -> source.instance == $it.instance && Utility.withinDistance(target, $it, Region.VIEW_DISTANCE)).forEach($it -> $it.send(new SendProjectile(projectile, center, lockon, offsetX, offsetY)));
	}
	
	/**
	 * Sends a world projectile.
	 */
	public static void sendProjectile(Actor source, Position target, Projectile projectile) {
		int sourceX = source.getX() + source.width() / 2, sourceY = source.getY() + source.length() / 2;
		Position center = new Position(sourceX, sourceY);
		byte offsetX = (byte) (target.getX() - sourceX);
		byte offsetY = (byte) (target.getY() - sourceY);
		get().players.stream().filter($it -> source.instance == $it.instance && Utility.withinDistance($it, target, 15)).forEach($it -> $it.send(new SendProjectile(projectile, center, -1, offsetX, offsetY)));
	}
	
	/**
	 * Sends a global message.
	 */
	public static void sendMessage(String... messages) {
		for(Player player : getPlayers()) {
			if(player != null)
				player.message(messages);
		}
	}
	
	/**
	 * Sends a global message with an exception.
	 */
	public static void sendMessage(String message, Predicate<Player> filter) {
		for(Player player : getPlayers()) {
			if(player != null && filter.test(player))
				player.message(message);
		}
	}
	
	public static void sendTeleportButtonNpc(int npcId) {
		for(Player player : getPlayers()) {
			player.send(new SendString("" + npcId, 45615));
		}
	}
	
	public static void sendTeleportButton() {
		for(Player player : getPlayers()) {
			player.send(new SendString("1", 45600));
		}
	}
	
	public static void hideTeleportButton() {
		for(Player player : getPlayers()) {
			player.send(new SendString("0", 45600));
			
		}
	}
	
	public static void sendStaffMessage(String... messages) {
		for(Player player : getStaff()) {
			player.message(messages);
		}
	}
	
	/**
	 * Sends a game message.
	 */
	public static void sendBroadcast(int time, String message, boolean countdown) {
		get().players.stream().forEach($it -> {
			$it.send(new SendGameMessage(countdown ? 0 : 1, time, Utility.capitalizeSentence(message)));
			$it.send(new SendMessage("[<col=2C7526>Runity</col>]" + Utility.capitalizeSentence(message)));
		});
	}
	
	public void dequeLogins() {
		if(logins.isEmpty()) {
			return;
		}
		
		for(int index = 0; index < Config.LOGIN_THESHOLD; index++) {
			Player player = logins.poll();
			
			if(player == null) {
				break;
			}
			
			try {
				player.register();
			} catch(Exception ex) {
				logger.error(String.format("error registering %s", player));
				ex.printStackTrace();
			}
			
		}
	}
	
	public void dequeLogouts() {
		if(logouts.isEmpty()) {
			return;
		}
		
		for(int index = 0; index < Config.LOGOUT_THESHOLD; index++) {
			Player player = logouts.poll();
			
			if(player == null) {
				break;
			}
			
			World.schedule(new PlayerRemovalTask(player, false));
		}
	}
	
	public int getWildernessResourcePlayers() {
		int count = 0;
		for(Player player : getPlayers()) {
			if(player != null && Area.inWildernessResource(player))
				count++;
		}
		return count;
	}
	
	/**
	 * Sends a kill feed notification.
	 */
	public static void sendKillFeed(String killer, Actor victim) {
		if(victim.isNpc() && (Area.inWilderness(victim) || !MobAssistant.STRATEGIES.containsKey(victim.id)))
			return;
		for(Player everyone : getPlayers())
			everyone.send(new SendKillFeed(killer, victim.getName()));
	}
	
	/**
	 * Gets the amount of valid playeys online.
	 *
	 * @return The amount of players online.
	 */
	
	/**
	 * Includes the bot count to the player count, with a additional +3
	 * @return
	 */
	
	public static int getPlayerCount() {
		/**
		 * I've verified this algorithm and decided it's ideal for what we're trying to
		 * do You can see a graph of the algorithm here:
		 * https://www.desmos.com/calculator/huuev2vzft
		 *
		 * Here's some example data from it: Authentic Player Count | Showed Player
		 * Count 1 2 //Small fakes, w/e 5 9 15 25 //Medium sized server, everyone knows
		 * eachother, 10 extra is easy 20 33 35 58 //Slightly larger server, payoff at
		 * 25+ fake 50 85 80 137 100 173 //Big fakes 150 263 200 361
		 */
		int p = getPlayers().size() - getBotCount();
		return (int) (1.5 * Math.sqrt(p) + Math.pow(p, 1.1)) - RandomUtils.inclusive(0, 1);
	}
	
	public static int getBotCount() {
		return PlayerBot.BOT_COUNT.get();
	}
	
	public static int getStaffCount() {
		int count = 0;
		
		for(Player player : getPlayers()) {
			if(player != null && (PlayerRight.isManagement(player) || player.right == PlayerRight.HELPER))
				count++;
		}
		
		return count;
	}
	
	public static int getWildernessCount() {
		int count = 0;
		
		for(Player player : getPlayers()) {
			if(player != null && player.isBot && Area.inWilderness(player))
				count++;
		}
		
		return count;
	}
	
	/**
	 * Gets the staff players currently online.
	 */
	public static List<Player> getStaff() {
		List<Player> staff = new ArrayList<>();
		
		for(Player player : getPlayers()) {
			if(player != null && (PlayerRight.isManagement(player) || player.right == PlayerRight.HELPER))
				staff.add(player);
		}
		
		return staff;
	}
	
	public static World get() {
		return WORLD;
	}
	
	public static void cancelTask(Object attachment) {
		cancelTask(attachment, false);
	}
	
	public static void cancelTask(Object attachment, boolean logout) {
		get().taskManager.cancel(attachment, logout);
	}
	
	public static MobList<Player> getPlayers() {
		return get().players;
	}
	
	public static MobList<Mob> getNpcs() {
		return get().npcs;
	}
	
	public static RegionManager getRegions() {
		return get().regionManager;
	}
	
	public TaskManager getTaskManager() {
		return taskManager;
	}
	
	public Queue<Player> getLogins() {
		return logins;
	}
	
	public Queue<Player> getLogouts() {
		return logouts;
	}
	
	public static DataBus getDataBus() {
		return dataBus;
	}
	
}
