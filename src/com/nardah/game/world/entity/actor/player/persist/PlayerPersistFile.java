package com.nardah.game.world.entity.actor.player.persist;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nardah.Config;
import com.nardah.content.ActivityLog;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.activity.impl.barrows.BrotherData;
import com.nardah.content.activity.impl.duelarena.DuelRule;
import com.nardah.content.clanchannel.ClanRepository;
import com.nardah.content.clanchannel.content.ClanMemberComporator;
import com.nardah.content.dailyachievement.DailyAchievementKey;
import com.nardah.content.emote.EmoteUnlockable;
import com.nardah.content.masterminer.MasterMinerTaskHandler;
import com.nardah.content.masterminer.MobData;
import com.nardah.content.pet.PetData;
import com.nardah.content.preset.Preset;
import com.nardah.content.prestige.PrestigePerk;
import com.nardah.content.skill.impl.magic.Spellbook;
import com.nardah.content.skill.impl.slayer.SlayerTask;
import com.nardah.content.skill.impl.slayer.SlayerUnlockable;
import com.nardah.content.teleport.Teleport;
import com.nardah.content.tittle.PlayerTitle;
import com.nardah.game.service.HighscoreService;
import com.nardah.game.world.entity.actor.player.appearance.Appearance;
import com.nardah.game.world.entity.actor.player.relations.PrivacyChatMode;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.entity.actor.prayer.PrayerBook;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.position.Position;
import com.nardah.util.AccountUtility;
import com.nardah.util.GsonUtils;
import com.nardah.util.Utility;
import com.nardah.content.clanchannel.channel.ClanChannel;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;
import com.nardah.net.codec.login.LoginResponse;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class PlayerPersistFile implements PlayerPersistable {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.SYSTEM);
	
	private static final Path FILE_DIR = Paths.get("data", "profile", "save");
	
	private static final Gson GSON = GsonUtils.JSON_PRETTY_ALLOW_NULL;
	
	@Override
	public void save(Player player) {
		if(player.isBot) {
			return;
		}
		
		HighscoreService.saveHighscores(player);
		
		new Thread(() -> {
			try {
				JsonObject properties = new JsonObject();
				
				for(PlayerJSONProperty property : PROPERTIES) {
					properties.add(property.label, GSON.toJsonTree(property.write(player)));
				}
				
				try {
					Files.write(FILE_DIR.resolve(player.getName() + ".json"), GSON.toJson(properties).getBytes());
				} catch(IOException e) {
					e.printStackTrace();
				}
			} catch(Exception ex) {
				logger.error(String.format("Error while saving player=%s", player.getName()));
				ex.printStackTrace();
			}
			
			player.saved.set(true);
		}).start();
	}
	
	@Override
	public LoginResponse load(Player player, String expectedPassword) {
		final File dir = FILE_DIR.toFile();
		
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		try {
			Path path = FILE_DIR.resolve(player.getName() + ".json");
			
			if(!Files.exists(path)) {
				player.newPlayer = true;
				player.needsStarter = true;
				return LoginResponse.NORMAL;
			}
			
			try(Reader reader = new FileReader(path.toFile())) {
				JsonObject jsonReader = (JsonObject) new JsonParser().parse(reader);
				
				for(PlayerJSONProperty property : PROPERTIES) {
					if(jsonReader.has(property.label)) {
						if(jsonReader.get(property.label).isJsonNull())
							continue;
						property.read(player, jsonReader.get(property.label));
					}
				}

				if (Config.FORUM_INTEGRATION) {
					LoginResponse response = LoginResponse.NORMAL;
					if (!AccountUtility.verify(player.getName(), expectedPassword)) {
						response = LoginResponse.INVALID_CREDENTIALS;
					}
					if (AccountUtility.isBanned(player.getName())) {
						response = LoginResponse.ACCOUNT_DISABLED;
					}
					return response;
				} else {

					if (!expectedPassword.equals(player.getPassword())) {
						return LoginResponse.INVALID_CREDENTIALS;
					}
				}
			}
			
			return LoginResponse.NORMAL;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return LoginResponse.COULD_NOT_COMPLETE_LOGIN;
	}
	
	private static final PlayerJSONProperty[] PROPERTIES = {
			
			new PlayerJSONProperty("username") {
				@Override
				void read(Player player, JsonElement property) {
					player.setUsername(property.getAsString());
				}
				
				@Override
				Object write(Player player) {
					return player.getUsername();
				}
			},

			new PlayerJSONProperty("password") {
				@Override
				void read(Player player, JsonElement property) {
					player.setPassword(property.getAsString());
				}

				@Override
				Object write(Player player) {
					return player.getPassword();
				}
			},
			
			new PlayerJSONProperty("player-rights") {
				@Override
				void read(Player player, JsonElement property) {
					player.right = GSON.fromJson(property, PlayerRight.class);
				}
				
				@Override
				Object write(Player player) {
					return player.right;
				}
			},
			
			new PlayerJSONProperty("last-host") {
				@Override
				void read(Player player, JsonElement property) {
					player.lastHost = property.getAsString();
				}
				
				@Override
				Object write(Player player) {
					return player.lastHost;
				}
			},
			
			new PlayerJSONProperty("host-list") {
				@Override
				void read(Player player, JsonElement property) {
					player.hostList.addAll(GSON.fromJson(property, new TypeToken<Set<String>>() {
					}.getType()));
				}
				
				@Override
				Object write(Player player) {
					return player.hostList;
				}
			},
			
			new PlayerJSONProperty("position") {
				@Override
				void read(Player player, JsonElement property) {
					final Position position = GSON.fromJson(property, Position.class);
					final int height = position.getHeight() < 0 ? 0 : position.getHeight() % 4;
					player.setPosition(Position.create(position.getX(), position.getY(), height));
				}
				
				@Override
				Object write(Player player) {
					return player.getPosition();
				}
			},
			
			new PlayerJSONProperty("created") {
				@Override
				void read(Player player, JsonElement property) {
					player.created = property.getAsString();
				}
				
				@Override
				Object write(Player player) {
					if(player.created == null)
						return Utility.getDate();
					return player.created;
					
				}
			},
			
			new PlayerJSONProperty("trivia-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.triviaPoints = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.triviaPoints;
				}
			},
			
			new PlayerJSONProperty("play-time") {
				@Override
				void read(Player player, JsonElement property) {
					player.playTime = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.playTime;
				}
			},
			
			new PlayerJSONProperty("money-spent") {
				@Override
				void read(Player player, JsonElement property) {
					player.donation.setSpent(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.donation.getSpent();
				}
			},
			
			new PlayerJSONProperty("donation-credits") {
				@Override
				void read(Player player, JsonElement property) {
					player.donation.setCredits(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.donation.getCredits();
				}
			},
			
			new PlayerJSONProperty("pest-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.pestPoints = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.pestPoints;
				}
			},
			
			new PlayerJSONProperty("mute-start") {
				@Override
				void read(Player player, JsonElement property) {
					player.punishment.muteStart = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.punishment.muteStart;
				}
			},
			
			new PlayerJSONProperty("mute-duration") {
				@Override
				void read(Player player, JsonElement property) {
					player.punishment.muteDuration = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.punishment.muteDuration;
				}
			},
			
			new PlayerJSONProperty("jail-start") {
				@Override
				void read(Player player, JsonElement property) {
					player.punishment.jailStart = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.punishment.jailStart;
				}
			},
			
			new PlayerJSONProperty("jail-duration") {
				@Override
				void read(Player player, JsonElement property) {
					player.punishment.jailDuration = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.punishment.jailDuration;
				}
			}, new PlayerJSONProperty("ban-start") {
		@Override
		void read(Player player, JsonElement property) {
			player.punishment.banStart = property.getAsLong();
		}
		
		@Override
		Object write(Player player) {
			return player.punishment.banStart;
		}
	},
			
			new PlayerJSONProperty("ban-duration") {
				@Override
				void read(Player player, JsonElement property) {
					player.punishment.banDuration = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.punishment.banDuration;
				}
			},
			
			new PlayerJSONProperty("player-title") {
				@Override
				void read(Player player, JsonElement property) {
					player.playerTitle = GSON.fromJson(property, PlayerTitle.class);
				}
				
				@Override
				Object write(Player player) {
					return player.playerTitle;
				}
			},
			
			new PlayerJSONProperty("clan") {
				@Override
				void read(Player player, JsonElement property) {
					ClanChannel channel = ClanRepository.getChannel(property.getAsString());
					if(channel != null) {
						player.clanChannel = channel;
					}
				}
				
				@Override
				Object write(Player player) {
					return player.clanChannel == null ? "" : player.clanChannel.getOwner();
				}
			},
			
			new PlayerJSONProperty("last-clan") {
				@Override
				void read(Player player, JsonElement property) {
					player.lastClan = property.getAsString();
				}
				
				@Override
				Object write(Player player) {
					return player.lastClan;
				}
			},
			
			new PlayerJSONProperty("clan-tag") {
				@Override
				void read(Player player, JsonElement property) {
					player.clanTag = property.getAsString();
				}
				
				@Override
				Object write(Player player) {
					return player.clanTag;
				}
			},
			
			new PlayerJSONProperty("clan-tag-color") {
				@Override
				void read(Player player, JsonElement property) {
					player.clanTagColor = property.getAsString();
				}
				
				@Override
				Object write(Player player) {
					return player.clanTagColor;
				}
			},
			
			new PlayerJSONProperty("clan-sort-type") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.clanMemberComporator = ClanMemberComporator.valueOf(property.getAsString());
				}
				
				@Override
				Object write(Player player) {
					return player.settings.clanMemberComporator.name();
				}
			},
			
			new PlayerJSONProperty("bank-placeholder") {
				@Override
				void read(Player player, JsonElement property) {
					player.bank.placeHolder = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.bank.placeHolder;
				}
			},
			
			new PlayerJSONProperty("bank-pin") {
				@Override
				void read(Player player, JsonElement property) {
					player.bankPin.pin = property.getAsString();
				}
				
				@Override
				Object write(Player player) {
					return player.bankPin.pin;
				}
			},
			
			new PlayerJSONProperty("bank-vault") {
				@Override
				void read(Player player, JsonElement property) {
					player.bankVault.container = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.bankVault.container;
				}
			},
			
			new PlayerJSONProperty("royalty") {
				@Override
				void read(Player player, JsonElement property) {
					player.royalty = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.royalty;
				}
			},
			
			new PlayerJSONProperty("royalty-level") {
				@Override
				void read(Player player, JsonElement property) {
					player.royaltyLevel = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.royaltyLevel;
				}
			},
			
			new PlayerJSONProperty("pet") {
				@Override
				void read(Player player, JsonElement property) {
					int pet = property.getAsInt();
					if(pet != -1) {
						player.pet = new Mob(pet, player.getPosition());
					}
				}
				
				@Override
				Object write(Player player) {
					return player.pet == null ? -1 : player.pet.id;
				}
			},
			
			new PlayerJSONProperty("pet-insurnce") {
				@Override
				void read(Player player, JsonElement property) {
					player.petInsurance = GSON.fromJson(property, new TypeToken<HashSet<PetData>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.petInsurance;
				}
			},
			
			new PlayerJSONProperty("lost-pets") {
				@Override
				void read(Player player, JsonElement property) {
					player.lostPets = GSON.fromJson(property, new TypeToken<HashSet<PetData>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.lostPets;
				}
			},
			
			new PlayerJSONProperty("new-player") {
				@Override
				void read(Player player, JsonElement property) {
					player.newPlayer = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.newPlayer;
				}
			},
			
			new PlayerJSONProperty("needs-starter") {
				@Override
				void read(Player player, JsonElement property) {
					player.needsStarter = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.needsStarter;
				}
			},
			
			new PlayerJSONProperty("player-index") {
				@Override
				void read(Player player, JsonElement property) {
					player.setIndex(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.getIndex();
				}
			},
			
			new PlayerJSONProperty("kills") {
				@Override
				void read(Player player, JsonElement property) {
					player.kill = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.kill;
				}
			},
			
			new PlayerJSONProperty("deaths") {
				@Override
				void read(Player player, JsonElement property) {
					player.death = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.death;
				}
			},
			
			new PlayerJSONProperty("kill-streak") {
				@Override
				void read(Player player, JsonElement property) {
					player.killstreak.streak = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.killstreak.streak;
				}
			},
			
			new PlayerJSONProperty("small-pouch") {
				@Override
				void read(Player player, JsonElement property) {
					player.smallPouch = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.smallPouch;
				}
			},
			
			new PlayerJSONProperty("medium-pouch") {
				@Override
				void read(Player player, JsonElement property) {
					player.mediumPouch = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.mediumPouch;
				}
			},
			
			new PlayerJSONProperty("large-pouch") {
				@Override
				void read(Player player, JsonElement property) {
					player.largePouch = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.largePouch;
				}
			},
			
			new PlayerJSONProperty("giant-pouch") {
				@Override
				void read(Player player, JsonElement property) {
					player.giantPouch = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.giantPouch;
				}
			},
			
			new PlayerJSONProperty("rune-pouch") {
				@Override
				void read(Player player, JsonElement property) {
					player.runePouch.runes = GSON.fromJson(property, new TypeToken<LinkedList<Item>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.runePouch.runes;
				}
			},
			
			new PlayerJSONProperty("run-toggled") {
				@Override
				void read(Player player, JsonElement property) {
					player.movement.setRunningToggled(property.getAsBoolean());
				}
				
				@Override
				Object write(Player player) {
					return player.movement.isRunningToggled();
				}
			},
			
			new PlayerJSONProperty("run-energy") {
				@Override
				void read(Player player, JsonElement property) {
					player.runEnergy = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.runEnergy;
				}
			},
			
			new PlayerJSONProperty("energy-rate") {
				@Override
				void read(Player player, JsonElement property) {
					player.energyRate = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.energyRate;
				}
			},
			
			new PlayerJSONProperty("special-amount") {
				@Override
				void read(Player player, JsonElement property) {
					player.getSpecialPercentage().set(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.getSpecialPercentage().get();
				}
			},
			
			new PlayerJSONProperty("recoil-charge") {
				@Override
				void read(Player player, JsonElement property) {
					player.ringOfRecoil = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.ringOfRecoil;
				}
			},
			
			new PlayerJSONProperty("dragonfire-shield-charge") {
				@Override
				void read(Player player, JsonElement property) {
					player.dragonfireCharges = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.dragonfireCharges;
				}
			},
			
			new PlayerJSONProperty("dragonfire-shield-cooldown") {
				@Override
				void read(Player player, JsonElement property) {
					player.dragonfireUsed = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.dragonfireUsed;
				}
			},
			
			new PlayerJSONProperty("blowpipe-darts") {
				@Override
				void read(Player player, JsonElement property) {
					player.blowpipeDarts = GSON.fromJson(property, Item.class);
				}
				
				@Override
				Object write(Player player) {
					return player.blowpipeDarts;
				}
			},
			
			new PlayerJSONProperty("blowpipe-scales") {
				@Override
				void read(Player player, JsonElement property) {
					player.blowpipeScales = property.getAsFloat();
				}
				
				@Override
				Object write(Player player) {
					return player.blowpipeScales;
				}
			},
			
			new PlayerJSONProperty("serpentine-helm") {
				@Override
				void read(Player player, JsonElement property) {
					player.serpentineHelmCharges = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.serpentineHelmCharges;
				}
			},
			
			new PlayerJSONProperty("tanzanite-helm") {
				@Override
				void read(Player player, JsonElement property) {
					player.tanzaniteHelmCharges = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.tanzaniteHelmCharges;
				}
			},
			
			new PlayerJSONProperty("magma-helm") {
				@Override
				void read(Player player, JsonElement property) {
					player.magmaHelmCharges = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.magmaHelmCharges;
				}
			},
			
			new PlayerJSONProperty("trident-seas-charges") {
				@Override
				void read(Player player, JsonElement property) {
					player.tridentSeasCharges = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.tridentSeasCharges;
				}
			},
			
			new PlayerJSONProperty("trident-swamp-charges") {
				@Override
				void read(Player player, JsonElement property) {
					player.tridentSwampCharges = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.tridentSwampCharges;
				}
			},
			
			new PlayerJSONProperty("fight-type") {
				@Override
				void read(Player player, JsonElement property) {
					player.getCombat().setFightType(GSON.fromJson(property, FightType.class));
				}
				
				@Override
				Object write(Player player) {
					return player.getCombat().getFightType();
				}
			},
			
			new PlayerJSONProperty("client-width") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.clientWidth = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.clientWidth;
				}
			},
			
			new PlayerJSONProperty("client-height") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.clientHeight = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.clientHeight;
				}
			},
			
			new PlayerJSONProperty("spell-book") {
				@Override
				void read(Player player, JsonElement property) {
					player.spellbook = GSON.fromJson(property, Spellbook.class);
				}
				
				@Override
				Object write(Player player) {
					return player.spellbook;
				}
			},
			
			new PlayerJSONProperty("brightness") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.setBrightness(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.settings.brightness;
				}
			},
			
			new PlayerJSONProperty("zoom") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.setZoom(property.getAsInt(), true);
				}
				
				@Override
				Object write(Player player) {
					return player.settings.zoom;
				}
			},
			
			new PlayerJSONProperty("private-chat") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.privateChat = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.privateChat;
				}
			},
			
			new PlayerJSONProperty("chat-effects") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.chatEffects = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.chatEffects;
				}
			},
			
			new PlayerJSONProperty("accept-aid") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.acceptAid = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.acceptAid;
				}
			},
			
			new PlayerJSONProperty("mouse-clicks") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.mouse = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.mouse;
				}
			},
			
			new PlayerJSONProperty("auto-retaliate") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.autoRetaliate = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.autoRetaliate;
				}
			},
			
			new PlayerJSONProperty("profanity-filter") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.profanityFilter = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.profanityFilter;
				}
			},
			
			new PlayerJSONProperty("camera-movement") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.cameraMovement = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.cameraMovement;
				}
			},
			
			new PlayerJSONProperty("experience-lock") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.lockExperience = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.lockExperience;
				}
			},
			
			new PlayerJSONProperty("welcome-screen") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.welcomeScreen = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.welcomeScreen;
				}
			},
			
			new PlayerJSONProperty("trivia-bot") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.triviaBot = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.triviaBot;
				}
			},
			
			new PlayerJSONProperty("drop-notification") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.dropNotification = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.dropNotification;
				}
			},
			
			new PlayerJSONProperty("yell-notification") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.yell = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.yell;
				}
			},
			
			new PlayerJSONProperty("untradeable-notification") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.untradeableNotification = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.untradeableNotification;
				}
			},
			
			new PlayerJSONProperty("esc-close") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.ESC_CLOSE = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.ESC_CLOSE;
				}
			},
			
			new PlayerJSONProperty("gloves-tier") {
				@Override
				void read(Player player, JsonElement property) {
					player.glovesTier = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.glovesTier;
				}
			},
			
			new PlayerJSONProperty("experience-rate") {
				@Override
				void read(Player player, JsonElement property) {
					player.experienceRate = property.getAsDouble();
				}
				
				@Override
				Object write(Player player) {
					return player.experienceRate;
				}
			},
			
			new PlayerJSONProperty("experience-counter") {
				@Override
				void read(Player player, JsonElement property) {
					player.skills.experienceCounter = property.getAsDouble();
				}
				
				@Override
				Object write(Player player) {
					return player.skills.experienceCounter;
				}
			},
			
			new PlayerJSONProperty("preset-death-open") {
				@Override
				void read(Player player, JsonElement property) {
					player.presetManager.deathOpen = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.presetManager.deathOpen;
				}
			},
			
			new PlayerJSONProperty("preset-death-open") {
				@Override
				void read(Player player, JsonElement property) {
					player.presetManager.deathOpen = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.presetManager.deathOpen;
				}
			},
			
			new PlayerJSONProperty("preset-automatic-deposit") {
				@Override
				void read(Player player, JsonElement property) {
					player.presetManager.autoDeposit = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.presetManager.autoDeposit;
				}
			},
			
			new PlayerJSONProperty("hidden-brother") {
				@Override
				void read(Player player, JsonElement property) {
					player.hiddenBrother = GSON.fromJson(property, BrotherData.class);
				}
				
				@Override
				Object write(Player player) {
					return player.hiddenBrother;
				}
			},
			
			new PlayerJSONProperty("brothers-kill-count") {
				@Override
				void read(Player player, JsonElement property) {
					player.barrowsKillCount = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.barrowsKillCount;
				}
			},
			
			new PlayerJSONProperty("brothers-dead") {
				@Override
				void read(Player player, JsonElement property) {
					player.barrowKills = GSON.fromJson(property, boolean[].class);
				}
				
				@Override
				Object write(Player player) {
					return player.barrowKills;
				}
			},
			
			new PlayerJSONProperty("vote-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.votePoints = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.votePoints;
				}
			},
			
			new PlayerJSONProperty("total-votes") {
				@Override
				void read(Player player, JsonElement property) {
					player.totalVotes = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.totalVotes;
				}
			},
			
			new PlayerJSONProperty("pk-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.pkPoints = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.pkPoints;
				}
			}, new PlayerJSONProperty("skilling-points") {
		@Override
		void read(Player player, JsonElement property) {
			player.skillingPoints = property.getAsInt();
		}
		
		@Override
		Object write(Player player) {
			return player.skillingPoints;
		}
	}, new PlayerJSONProperty("boss-points") {
		@Override
		void read(Player player, JsonElement property) {
			player.bossPoints = property.getAsInt();
		}
		
		@Override
		Object write(Player player) {
			return player.bossPoints;
		}
	},
			
			new PlayerJSONProperty("kolodion-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.kolodionPoints = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.kolodionPoints;
				}
			},
			
			new PlayerJSONProperty("poison-immunity") {
				@Override
				void read(Player player, JsonElement property) {
					player.getPoisonImmunity().set(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.getPoisonImmunity().get();
				}
			},
			
			new PlayerJSONProperty("venom-immunity") {
				@Override
				void read(Player player, JsonElement property) {
					player.getVenomImmunity().set(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.getVenomImmunity().get();
				}
			},
			
			new PlayerJSONProperty("skull-timer") {
				@Override
				void read(Player player, JsonElement property) {
					player.skulling.getSkullRemoveTask().setSkullTime(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.skulling.getSkullRemoveTask().getSkullTime();
				}
			},
			
			new PlayerJSONProperty("teleblock-timer") {
				@Override
				void read(Player player, JsonElement property) {
					player.teleblockTimer.set(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.teleblockTimer.get();
				}
			},
			
			new PlayerJSONProperty("prestige-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.prestige.setPrestigePoint(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.prestige.getPrestigePoint();
				}
			},
			
			new PlayerJSONProperty("prestige-total") {
				@Override
				void read(Player player, JsonElement property) {
					player.prestige.totalPrestige = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.prestige.totalPrestige;
				}
			},
			
			new PlayerJSONProperty("prestige-color") {
				@Override
				void read(Player player, JsonElement property) {
					player.settings.prestigeColors = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.settings.prestigeColors;
				}
			},
			
			new PlayerJSONProperty("prestiges") {
				@Override
				void read(Player player, JsonElement property) {
					player.prestige.prestige = GSON.fromJson(property, int[].class);
				}
				
				@Override
				Object write(Player player) {
					return player.prestige.prestige;
				}
			},
			
			new PlayerJSONProperty("active-perks") {
				@Override
				void read(Player player, JsonElement property) {
					player.prestige.activePerks = GSON.fromJson(property, new TypeToken<Set<PrestigePerk>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.prestige.activePerks;
				}
			},
			
			new PlayerJSONProperty("quest-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.quest.setPoints(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.quest.getPoints();
				}
			},
			
			new PlayerJSONProperty("quest-completed") {
				@Override
				void read(Player player, JsonElement property) {
					player.quest.setCompleted(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.quest.getCompleted();
				}
			},
			
			new PlayerJSONProperty("quest-stage") {
				@Override
				void read(Player player, JsonElement property) {
					int[] stages = GSON.fromJson(property, int[].class);
					System.arraycopy(stages, 0, player.quest.stage, 0, stages.length);
				}
				
				@Override
				Object write(Player player) {
					return player.quest.stage;
				}
			},
			
			new PlayerJSONProperty("achievement") {
				@Override
				void read(Player player, JsonElement property) {
					player.playerAchievements = GSON.fromJson(property, new TypeToken<HashMap<AchievementKey, Integer>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return GSON.toJsonTree(player.playerAchievements, new TypeToken<HashMap<AchievementKey, Integer>>() {
					}.getType());
				}
			}, new PlayerJSONProperty("daily-achievement") {
		@Override
		void read(Player player, JsonElement property) {
			player.playerAchievements1 = GSON.fromJson(property, new TypeToken<HashMap<DailyAchievementKey, Integer>>() {
			}.getType());
		}
		
		@Override
		Object write(Player player) {
			return GSON.toJsonTree(player.playerAchievements1, new TypeToken<HashMap<DailyAchievementKey, Integer>>() {
			}.getType());
		}
	},
			
			new PlayerJSONProperty("last-killed") {
				@Override
				void read(Player player, JsonElement property) {
					player.lastKilled = GSON.fromJson(property, new TypeToken<Deque<String>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.lastKilled;
				}
			},
			
			new PlayerJSONProperty("favorite-teleport") {
				@Override
				void read(Player player, JsonElement property) {
					player.favoriteTeleport = GSON.fromJson(property, new TypeToken<List<Teleport>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.favoriteTeleport;
				}
			},
			
			new PlayerJSONProperty("unlocked-emotes") {
				@Override
				void read(Player player, JsonElement property) {
					player.emoteUnlockable = GSON.fromJson(property, new TypeToken<List<EmoteUnlockable>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.emoteUnlockable;
				}
			},
			
			new PlayerJSONProperty("public-chat-mode") {
				@Override
				void read(Player player, JsonElement property) {
					player.relations.setPublicChatMode(PrivacyChatMode.valueOf(GSON.fromJson(property, String.class).toUpperCase()), false);
				}
				
				@Override
				Object write(Player player) {
					return player.relations.getPublicChatMode().name();
				}
			},
			
			new PlayerJSONProperty("private-chat-mode") {
				@Override
				void read(Player player, JsonElement property) {
					player.relations.setPrivateChatMode(PrivacyChatMode.valueOf(GSON.fromJson(property, String.class).toUpperCase()), true);
				}
				
				@Override
				Object write(Player player) {
					return player.relations.getPrivateChatMode().name();
				}
			},
			
			new PlayerJSONProperty("clan-chat-mode") {
				@Override
				void read(Player player, JsonElement property) {
					player.relations.setClanChatMode(PrivacyChatMode.valueOf(GSON.fromJson(property, String.class).toUpperCase()), false);
				}
				
				@Override
				Object write(Player player) {
					return player.relations.getClanChatMode().name();
				}
			},
			
			new PlayerJSONProperty("trade-chat-mode") {
				@Override
				void read(Player player, JsonElement property) {
					player.relations.setTradeChatMode(PrivacyChatMode.valueOf(GSON.fromJson(property, String.class).toUpperCase()), false);
				}
				
				@Override
				Object write(Player player) {
					return player.relations.getTradeChatMode().name();
				}
			},
			
			new PlayerJSONProperty("friend-list") {
				@Override
				void read(Player player, JsonElement property) {
					player.relations.getFriendList().addAll(GSON.fromJson(property, new TypeToken<List<Long>>() {
					}.getType()));
				}
				
				@Override
				Object write(Player player) {
					return player.relations.getFriendList();
				}
			},
			
			new PlayerJSONProperty("ignore-list") {
				@Override
				void read(Player player, JsonElement property) {
					player.relations.getIgnoreList().addAll(GSON.fromJson(property, new TypeToken<List<Long>>() {
					}.getType()));
				}
				
				@Override
				Object write(Player player) {
					return player.relations.getIgnoreList();
				}
			},
			
			new PlayerJSONProperty("appearance") {
				@Override
				void read(Player player, JsonElement property) {
					player.appearance = GSON.fromJson(property, Appearance.class);
				}
				
				@Override
				Object write(Player player) {
					return player.appearance;
				}
			},
			
			new PlayerJSONProperty("preset") {
				@Override
				void read(Player player, JsonElement property) {
					Preset[] loaded = GSON.fromJson(property, Preset[].class);
					for(int idx = 0; idx < loaded.length; idx++) {
						player.presetManager.preset[idx] = loaded[idx];
					}
				}
				
				@Override
				Object write(Player player) {
					return player.presetManager.preset;
				}
			},
			
			new PlayerJSONProperty("activity-logger") {
				@Override
				void read(Player player, JsonElement property) {
					player.loggedActivities = GSON.fromJson(property, new TypeToken<HashMap<ActivityLog, Integer>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return GSON.toJsonTree(player.loggedActivities, new TypeToken<HashMap<ActivityLog, Integer>>() {
					}.getType());
				}
			},
			
			new PlayerJSONProperty("quick-prayers") {
				@Override
				void read(Player player, JsonElement property) {
					player.quickPrayers = GSON.fromJson(property, PrayerBook.class);
				}
				
				@Override
				Object write(Player player) {
					return player.quickPrayers;
				}
			},
			
			new PlayerJSONProperty("locked-prayers") {
				@Override
				void read(Player player, JsonElement property) {
					player.unlockedPrayers = GSON.fromJson(property, new TypeToken<Set<Prayer>>() {
					}.getType());
				}
				
				@Override
				Object write(Player player) {
					return player.unlockedPrayers;
				}
			},
			
			new PlayerJSONProperty("slayer-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setPoints(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getPoints();
				}
			},
			
			new PlayerJSONProperty("slayer-task") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setTask(GSON.fromJson(property, SlayerTask.class));
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getTask();
				}
			},
			
			new PlayerJSONProperty("slayer-assigned") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setAssigned(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getAssigned();
				}
			},
			
			new PlayerJSONProperty("slayer-amount") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setAmount(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getAmount();
				}
			},
			
			new PlayerJSONProperty("slayer-total-assigned") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setTotalAssigned(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getTotalAssigned();
				}
			},
			
			new PlayerJSONProperty("slayer-total-completed") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setTotalCompleted(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getTotalCompleted();
				}
			},
			
			new PlayerJSONProperty("slayer-total-cancelled") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setTotalCancelled(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getTotalCancelled();
				}
			},
			
			new PlayerJSONProperty("slayer-total-points") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setTotalPoints(property.getAsInt());
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getTotalPoints();
				}
			},
			
			new PlayerJSONProperty("slayer-blocked") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setBlocked(GSON.fromJson(property, new TypeToken<List<SlayerTask>>() {
					}.getType()));
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getBlocked();
				}
			},
			
			new PlayerJSONProperty("slayer-unlocked") {
				@Override
				void read(Player player, JsonElement property) {
					player.slayer.setUnlocked(GSON.fromJson(property, new TypeToken<Set<SlayerUnlockable>>() {
					}.getType()));
				}
				
				@Override
				Object write(Player player) {
					return player.slayer.getUnlocked();
				}
			},
			
			new PlayerJSONProperty("achieved-skills") {
				@Override
				void read(Player player, JsonElement property) {
					player.achievedSkills = GSON.fromJson(property, int[].class);
				}
				
				@Override
				Object write(Player player) {
					return player.achievedSkills;
				}
			},
			
			new PlayerJSONProperty("achieved-exp") {
				@Override
				void read(Player player, JsonElement property) {
					player.achievedExp = GSON.fromJson(property, double[].class);
				}
				
				@Override
				Object write(Player player) {
					return player.achievedExp;
				}
			},
			
			new PlayerJSONProperty("skills") {
				@Override
				void read(Player player, JsonElement property) {
					player.skills.setSkills(GSON.fromJson(property, Skill[].class));
				}
				
				@Override
				Object write(Player player) {
					return player.skills.getSkills();
				}
			},
			
			new PlayerJSONProperty("inventory") {
				@Override
				void read(Player player, JsonElement property) {
					player.inventory.set(GSON.fromJson(property, Item[].class));
				}
				
				@Override
				Object write(Player player) {
					return player.inventory.getItems();
				}
			},
			
			new PlayerJSONProperty("equipment") {
				@Override
				void read(Player player, JsonElement property) {
					player.equipment.set(GSON.fromJson(property, Item[].class));
				}
				
				@Override
				Object write(Player player) {
					return player.equipment.getItems();
				}
			},
			
			new PlayerJSONProperty("tab-amounts") {
				@Override
				void read(Player player, JsonElement property) {
					player.bank.tabAmounts = GSON.fromJson(property, int[].class);
				}
				
				@Override
				Object write(Player player) {
					return player.bank.tabAmounts;
				}
			},
			
			new PlayerJSONProperty("bank") {
				@Override
				void read(Player player, JsonElement property) {
					player.bank.set(GSON.fromJson(property, Item[].class));
				}
				
				@Override
				Object write(Player player) {
					return player.bank.getItems();
				}
			},
			
			new PlayerJSONProperty("looting-bag") {
				@Override
				void read(Player player, JsonElement property) {
					player.lootingBag.set(GSON.fromJson(property, Item[].class));
				}
				
				@Override
				Object write(Player player) {
					return player.lootingBag.getItems();
				}
			},
			
			new PlayerJSONProperty("lost-untradables") {
				@Override
				void read(Player player, JsonElement property) {
					player.lostUntradeables.set(GSON.fromJson(property, Item[].class));
				}
				
				@Override
				Object write(Player player) {
					return player.lostUntradeables.getItems();
				}
			},
			
			new PlayerJSONProperty("duel-rules") {
				@Override
				void read(Player player, JsonElement property) {
					if(property.isJsonNull()) {
						return;
					}
					Type ruleSet = new TypeToken<EnumSet<DuelRule>>() {
					}.getType();
					
					player.attributes.put("duel_rules", GSON.fromJson(property, ruleSet));
				}
				
				@Override
				Object write(Player player) {
					if(player.attributes.has("duel_rules")) {
						EnumSet<DuelRule> flags = player.attributes.get("duel_rules");
						return flags;
					}
					return null;
				}
			},
			
			new PlayerJSONProperty("gold-per-sec") {
				@Override
				void read(Player player, JsonElement property) {
					player.masterMinerData.goldPerSec = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.masterMinerData.goldPerSec;
				}
			},
			
			new PlayerJSONProperty("time-last-checked-ms") {
				@Override
				void read(Player player, JsonElement property) {
					player.masterMinerData.timeLastCheckedMs = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.masterMinerData.timeLastCheckedMs;
				}
			},
			
			new PlayerJSONProperty("total-gold") {
				@Override
				void read(Player player, JsonElement property) {
					player.masterMinerData.totalGold = property.getAsLong();
				}
				
				@Override
				Object write(Player player) {
					return player.masterMinerData.totalGold;
				}
			},
			
			new PlayerJSONProperty("actor-data") {
				@Override
				void read(Player player, JsonElement property) {
					String toParse = property.getAsString();
					String mob[] = toParse.split("<");
					for(int i = 0; i < 8; i++) {
						player.masterMinerData.mobData[i] = new MobData(mob[i]);
					}
				}
				
				@Override
				Object write(Player player) {
					String toWrite = "";
					for(int i = 0; i < 8; i++) {
						toWrite += player.masterMinerData.mobData[i] + "<";
					}
					return toWrite;
				}
			},
			
			new PlayerJSONProperty("amount-to-buy") {
				@Override
				void read(Player player, JsonElement property) {
					player.masterMinerData.amountToBuy = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.masterMinerData.amountToBuy;
				}
			},
			
			new PlayerJSONProperty("total-clicks") {
				@Override
				void read(Player player, JsonElement property) {
					player.masterMinerData.totalClicks = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.masterMinerData.totalClicks;
				}
			},
			
			new PlayerJSONProperty("prestige-level") {
				@Override
				void read(Player player, JsonElement property) {
					player.masterMinerData.prestigeLevel = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.masterMinerData.prestigeLevel;
				}
			},
			
			new PlayerJSONProperty("task-handler") {
				@Override
				void read(Player player, JsonElement property) {
					player.masterMinerTask = new MasterMinerTaskHandler(property.getAsString());
				}
				
				@Override
				Object write(Player player) {
					return player.masterMinerTask.toString();
				}
			},
			
			new PlayerJSONProperty("lms-coffer") {
				@Override
				void read(Player player, JsonElement property) {
					player.lmsCoffer = property.getAsInt();
				}
				
				@Override
				Object write(Player player) {
					return player.lmsCoffer;
				}
			},
			
			new PlayerJSONProperty("exp-rate") {
				@Override
				void read(Player player, JsonElement property) {
					double exp = property.getAsDouble();
					player.expRate = exp == 0.0 ? 1 : exp;
				}
				
				@Override
				Object write(Player player) {
					return player.expRate;
				}
			},
			
			new PlayerJSONProperty("brutal-mode") {
				@Override
				void read(Player player, JsonElement property) {
					player.brutalMode = property.getAsBoolean();
				}
				
				@Override
				Object write(Player player) {
					return player.brutalMode;
				}
			}, new PlayerJSONProperty("toolkit") {
		@Override
		void read(Player player, JsonElement property) {
			player.toolkit.set(GSON.fromJson(property, Item[].class));
		}
		
		@Override
		Object write(Player player) {
			return player.toolkit.getItems();
		}
	},};
	
}
