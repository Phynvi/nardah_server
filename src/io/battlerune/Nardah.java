package io.battlerune;

import io.battlerune.content.WellOfGoodwill;
import io.battlerune.content.activity.record.GlobalRecords;
import io.battlerune.content.clanchannel.ClanRepository;
import io.battlerune.content.itemaction.ItemActionRepository;
import io.battlerune.content.mysterybox.MysteryBox;
import io.battlerune.content.skill.SkillRepository;
import io.battlerune.content.store.PersonalStoreSaver;
import io.battlerune.content.triviabot.TriviaBot;
import io.battlerune.fs.cache.FileSystem;
import io.battlerune.fs.cache.decoder.MapDefinitionDecoder;
import io.battlerune.fs.cache.decoder.ObjectDefinitionDecoder;
import io.battlerune.fs.cache.decoder.RegionDecoder;
import io.battlerune.game.engine.GameEngine;
import io.battlerune.game.plugin.PluginManager;
import io.battlerune.game.service.NetworkService;
import io.battlerune.game.service.StartupService;
import io.battlerune.game.task.impl.*;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.attack.listener.CombatListenerManager;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.arena.ArenaEvent;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.galvek.GalvekEvent;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.magearena.DerwenEvent;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.magearena.JusticarEvent;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.magearena.PorazdirEvent;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.skotizo.SkotizoEvent;
import io.battlerune.game.world.entity.mob.npc.definition.NpcDefinition;
import io.battlerune.game.world.entity.mob.player.profile.ProfileRepository;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.io.PacketListenerLoader;
import io.battlerune.util.GameSaver;
import io.battlerune.util.Stopwatch;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;
import io.battlerune.util.parser.impl.*;
import io.battlerune.util.sql.MySqlConnector;
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
			FileSystem fs = FileSystem.create(System.getProperty("user.home") + "/NR/");
			new ObjectDefinitionDecoder(fs).run();
			new MapDefinitionDecoder(fs).run();
			new RegionDecoder(fs).run();
		} catch(IOException e) {
			e.printStackTrace();
		}
		ItemDefinition.createParser().run();
		NpcDefinition.createParser().run();
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
		// World.schedule(new DiscordEvent());
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
	
}
