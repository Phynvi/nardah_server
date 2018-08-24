package com.nardah;

import com.nardah.content.WellOfGoodwill;
import com.nardah.content.activity.record.GlobalRecords;
import com.nardah.content.clanchannel.ClanRepository;
import com.nardah.content.itemaction.ItemActionRepository;
import com.nardah.content.mysterybox.MysteryBox;
import com.nardah.content.skill.SkillRepository;
import com.nardah.content.store.PersonalStoreSaver;
import com.nardah.content.triviabot.TriviaBot;
import com.nardah.fs.cache.FileSystem;
import com.nardah.fs.cache.decoder.MapDefinitionDecoder;
import com.nardah.fs.cache.decoder.ObjectDefinitionDecoder;
import com.nardah.fs.cache.decoder.RegionDecoder;
import com.nardah.game.engine.GameEngine;
import com.nardah.game.plugin.PluginManager;
import com.nardah.game.service.NetworkService;
import com.nardah.game.service.StartupService;
import com.nardah.game.task.impl.*;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.profile.ProfileRepository;
import com.nardah.game.world.entity.combat.attack.listener.CombatListenerManager;
import com.nardah.game.world.entity.combat.strategy.npc.boss.arena.ArenaEvent;
import com.nardah.game.world.entity.combat.strategy.npc.boss.galvek.GalvekEvent;
import com.nardah.game.world.entity.combat.strategy.npc.boss.magearena.DerwenEvent;
import com.nardah.game.world.entity.combat.strategy.npc.boss.magearena.JusticarEvent;
import com.nardah.game.world.entity.combat.strategy.npc.boss.magearena.PorazdirEvent;
import com.nardah.game.world.entity.combat.strategy.npc.boss.skotizo.SkotizoEvent;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.io.PacketListenerLoader;
import com.nardah.util.GameSaver;
import com.nardah.util.Stopwatch;
import com.nardah.util.database.MySQLDatabase;
import com.nardah.util.parser.impl.*;
import com.nardah.util.sql.MySqlConnector;
import com.nardah.game.world.entity.actor.mob.MobDefinition;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;
import plugin.click.item.ClueScrollPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Nardah {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.START_UP);
	
	public static final AtomicBoolean serverStarted = new AtomicBoolean(false);
	public static final Stopwatch UPTIME = Stopwatch.start();
	
	private static final StartupService startupService = new StartupService();
	private static final GameEngine gameService = new GameEngine();
	private static final NetworkService networkService = new NetworkService();
	
	private static final Nardah INSTANCE = new Nardah();

	private static final MySQLDatabase database = new MySQLDatabase(Config.GLOBAL_DATABASE);
	
	private Nardah() {
	
	}
	
	private void processSequentialStatupTasks() {
		logger.info("Preparing Sequential Start Up Tasks...");
		if(Config.MY_SQL) {
			try {
				MySqlConnector.run();
			} catch(ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			logger.info("Preparing Object/Region Decoding...");
			new ObjectRemovalParser().run();
			FileSystem fs = FileSystem.create(System.getProperty("user.home") + "/nardah/");
			new ObjectDefinitionDecoder(fs).run();
			new MapDefinitionDecoder(fs).run();
			new RegionDecoder(fs).run();
		} catch(IOException e) {
			e.printStackTrace();
		}
		ItemDefinition.createParser().run();
		MobDefinition.createParser().run();
		new CombatProjectileParser().run();
		CombatListenerManager.load();
		new NpcSpawnParser().run();
		new NpcDropParser().run();
		new NpcForceChatParser().run();
		new StoreParser().run();
		new GlobalObjectParser().run();
		// MySqlLogHandler.run(MySqlCommands.INSERT, "Harryl has used ::commands");
	}
	
	/**
	 * Called after the sequential startup tasks, use this for faster startup. Try
	 * not to use this method for tasks that rely on other tasks or you'll run into
	 * issues.
	 */
	private void processParallelStatupTasks() {
		startupService.submit(new PacketSizeParser());
		startupService.submit(new PacketListenerLoader());
		startupService.submit(TriviaBot::declare);
		startupService.submit(PersonalStoreSaver::loadPayments);
		startupService.submit(ClanRepository::loadChannels);
		startupService.submit(GlobalRecords::load);
		startupService.submit(SkillRepository::load);
		startupService.submit(ProfileRepository::load);
		startupService.submit(ItemActionRepository::declare);
		startupService.submit(ClueScrollPlugin::declare);
		startupService.submit(MysteryBox::load);
		// startupService.submit(Discord::start);
		startupService.submit(GameSaver::load);
		startupService.shutdown();
	}
	
	/**
	 * Called when the game engine is running and all the startup tasks have
	 * finished loading
	 */
	private void onStart() {
		if(WellOfGoodwill.isActive()) {
			World.schedule(new DoubleExperienceEvent());
		}
		
		World.schedule(new MessageEvent());
		World.schedule(new ClanUpdateEvent());
		World.schedule(new PlayerSaveEvent());
		//World.schedule(new BotStartupEvent());
		//World.schedule(new DiscordEvent());
		World.schedule(new SkotizoEvent());
		World.schedule(new ArenaEvent());
		// World.schedule(new GanoEvent());
		World.schedule(new GalvekEvent());
		World.schedule(new JusticarEvent());
		World.schedule(new PorazdirEvent());
		World.schedule(new DerwenEvent());
		World.schedule(new RoyaltyEvent());
		logger.info("Events have been scheduled");
		logger.info("World Schdule Events have loaded.");
	}
	
	public void start() throws Exception {
		if (Config.FORUM_INTEGRATION) {
//            ForumService.start(); // used to check users logging in with website credentials
			getDatabase().prepare("nardah.com", "runity_root", "JNfIn3IvH5$V");

			if (Config.LIVE_SERVER) {
//                PostgreService.start(); // used to start the postgres connection pool
//                WebsitePlayerCountService.getInstance().startAsync(); // used to display player count on website
			}
		}

		
		logger.info(String.format("Starting %s Game Engine", Config.PARALLEL_GAME_ENGINE ? "Parallel" : "Sequential"));
		processSequentialStatupTasks();
		processParallelStatupTasks();
		
		startupService.awaitUntilFinished(5, TimeUnit.MINUTES);
		logger.info("Startup service finished");
		
		PluginManager.load();
		
		gameService.startAsync().awaitRunning();
		logger.info("Game service started");
		
		onStart();
		
		networkService.start(Config.SERVER_PORT);
	}
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Shutting down server, initializing shutdown hook");
			World.save();
		}));
		
		try {
			INSTANCE.start();
		} catch(Throwable t) {
			logger.error("A problem has been encountered while starting the server.");
			t.printStackTrace();
		}
		
	}
	
	public static Nardah getInstance() {
		return INSTANCE;
	}

	public static MySQLDatabase getDatabase() {
		return database;
	}

	
}
