package com.nardah.game.world.entity.actor.player;

import com.nardah.content.StarterKit;
import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.content.activity.GroupActivity;
import com.nardah.content.activity.impl.JailActivity;
import com.nardah.content.activity.impl.TutorialActivity;
import com.nardah.content.activity.impl.warriorguild.WarriorGuild;
import com.nardah.content.clanchannel.channel.ClanChannelHandler;
import com.nardah.content.dialogue.Expression;
import com.nardah.content.emote.EmoteHandler;
import com.nardah.content.pet.Pets;
import com.nardah.content.skill.impl.magic.teleport.TeleportType;
import com.nardah.content.writer.InterfaceWriter;
import com.nardah.content.writer.impl.InformationWriter;
import com.nardah.game.world.entity.actor.player.data.ValueIcon;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.entity.combat.CombatConstants;
import com.nardah.game.world.entity.combat.CombatTarget;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.FormulaFactory;
import com.nardah.game.world.entity.combat.attack.listener.other.PrayerListener;
import com.nardah.game.world.entity.combat.effect.CombatEffectType;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import com.nardah.game.world.entity.combat.strategy.player.custom.*;
import com.nardah.game.world.entity.combat.strategy.player.special.CombatSpecial;
import com.nardah.game.world.entity.combat.strategy.player.special.melee.ToragHammers;
import com.nardah.game.world.entity.combat.weapon.WeaponInterface;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.position.Area;
import com.nardah.net.packet.out.*;
import com.nardah.util.TinterfaceText;
import com.nardah.Config;
import com.nardah.game.task.impl.SuperAntipoisonTask;
import com.nardah.game.task.impl.TeleblockTask;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.mob.MobDefinition;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.ItemContainer;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.game.world.items.containers.pricechecker.PriceType;
import com.nardah.net.packet.out.SendWidget.WidgetType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Method handles small methods for players that do not have any parent class.
 * @author Daniel | Obey
 * @author Adam_#6723
 */
public class PlayerAssistant {
	
	/**
	 * The player instance.
	 */
	private final Player player;
	
	/**
	 * Holds itemcontainer strings.
	 */
	private Map<Integer, TinterfaceText> interfaceText = new HashMap<>();
	
	/**
	 * Creates a new <code>PlayerAssistant<code>
	 */
	PlayerAssistant(Player player) {
		this.player = player;
	}
	
	/**
	 * Handles initializing all the player assistant methods on login.
	 */
	public final void login() {
		reset();
		setAttribute();
		setPrayer();
		initialize();
		setSidebar(false);
		setActivity();
		setContextMenu();
		setEffects();
		Pets.onLogin(player);
		EmoteHandler.refresh(player);
		ClanChannelHandler.onLogin(player);
		player.getFarming().doConfig();
	}
	
	/**
	 * Sets the effects for the player.
	 */
	private void setEffects() {
		if(player.getPoisonImmunity().get() > 0) {
			World.schedule(new SuperAntipoisonTask(player).attach(player));
		}
		if(player.skulling.getSkullRemoveTask().getSkullTime() > 0) {
			player.skulling.skull();
		}
		
		if(player.teleblockTimer.get() > 0) {
			player.send(new SendWidget(SendWidget.WidgetType.TELEBLOCK, (int) ((double) player.teleblockTimer.get() / 1000D * 600D)));
			World.schedule(new TeleblockTask(player));
		}
		
	}
	
	/**
	 * initializes the player's random bs.
	 */
	private void initialize() {
		// Toolkit.TOOLS.forEach(t -> player.toolkit.fill(t.getId()));
		player.getCombat().resetTimers(-CombatConstants.COMBAT_LOGOUT_COOLDOWN);
		player.send(new SendEntityFeed(null, 0, 0));
		player.send(new SendString(PlayerRight.isManagement(player) ? "Open management panel" : "www.nardah.com", 29404));
		player.send(new SendTooltip(PlayerRight.isManagement(player) ? "Open management panel" : "Open website www.nardah.com", 29404));
		player.send(new SendSpecialAmount());
		InterfaceWriter.write(new InformationWriter(player));
	}
	
	/**
	 * Handles the method that will occur on sequence.
	 */
	public void sequence() {
		skillRestore();
		updateSpecial();
		prayerDrain();
		run();
		runRestore();
		player.getCombat().tick();
		Activity.forActivity(player, it -> {
			if(!(it instanceof GroupActivity))
				it.sequence();
		});
		CombatTarget.checkAggression(player);
		player.getFarming().sequence();
	}
	
	private Player lastTarget;
	
	public void sendOpponentStatsInterface(boolean on, Player other) {
		if(on) {
			if(lastTarget == other) {
				return;
			}
			lastTarget = other;
			int[] order = {0, 2, 1, 3, 4, 6, 5};
			for(int i = 0; i < 7; ++i) {
				int mySkill = player.skills.get(order[i]).getMaxLevel();
				int theirSkill = other.skills.get(order[i]).getMaxLevel();
				String myColor = "";
				String theirColor = "";
				if(mySkill > theirSkill) {
					myColor = "@gre@";
					theirColor = "@red@";
				} else if(mySkill < theirSkill) {
					myColor = "@red@";
					theirColor = "@gre@";
				}
				player.send(new SendString(myColor + mySkill, 23063 + i));
				player.send(new SendString(theirColor + theirSkill, 23070 + i));
			}
		} else {
			lastTarget = null;
		}
		player.send(new SendString(on ? "on" : "off", 23050));
	}
	
	/**
	 * Handles getting the combat strategy.
	 */
	public CombatStrategy<Player> getStrategy() {
		if(player.isSingleCast())
			return new PlayerMagicStrategy(player.getSingleCastSpell());
		if(player.isAutocast())
			return new PlayerMagicStrategy(player.getAutocastSpell());
		if(player.isSpecialActivated()) {
			if(player.getCombatSpecial() == null) {
				player.setSpecialActivated(false);
			} else {
				return player.getCombatSpecial().getStrategy();
			}
		}
		Item item = player.equipment.get(Equipment.WEAPON_SLOT);
		if(item != null) {
			if(item.getId() == 20997) {
				return TwistedBowStrategy.get();
			}
			if(item.getId() == 3274) {
				return FireyBowStrategy.get();
			}
			if(item.getId() == 21012) {
				return DragonHunterCrossbowStrategy.get();
			}
			
			if(item.getId() == 21225) {
				return LimeWhipStrategy.get();
			}
			if(item.getId() == 13111) {
				return ValyrianSwordStrategy.get();
			}
			if(item.getId() == 11063) {
				return ToxicGlaiveStrategy.get();
			}
			
			if(item.getId() == 11907) {
				return TridentOfTheSeasStrategy.get();
			}
			
			if(item.getId() == 12899) {
				return TridentOfTheSwampStrategy.get();
			}
			
			if(item.getId() == 12926) {
				return ToxicBlowpipeStrategy.get();
			}
			
			if(item.getId() == 4747) {
				return ToragHammers.get();
			}

			if (item.getId() == 22325) {
				return ScytheOfVitur.get();
			}
			
			switch(item.getId()) {
				case 839:
				case 845:
				case 847:
				case 851:
				case 855:
				case 859:
					return LongbowStrategy.get();
			}
			
			if(item.getRangedDefinition().isPresent()) {
				return PlayerRangedStrategy.get();
			}
			
		}
		return PlayerMeleeStrategy.get();
	}
	
	/**
	 * Updates the special amount.
	 */
	private void updateSpecial() {
		if(player.getSpecialPercentage().get() < 100 && player.sequence % 60 == 0)
			CombatSpecial.restore(player, 10);
	}
	
	/**
	 * Sends all the side-bar identifications to the {@code Player}'s client.
	 **/
	public final void setSidebar(boolean disabled) {
		for(int index = 0; index < Config.SIDEBAR_INTERFACE.length; index++) {
			player.interfaceManager.setSidebar(Config.SIDEBAR_INTERFACE[index][0], disabled ? -1 : Config.SIDEBAR_INTERFACE[index][1]);
		}
		player.interfaceManager.setSidebar(Config.MAGIC_TAB, disabled ? -1 : player.spellbook.getInterfaceId());
		if(!disabled) {
			WeaponInterface.execute(player, player.equipment.getWeapon());
			player.send(new SendConfig(980, 0));
		}
	}
	
	/**
	 * Sets all the player attributes.
	 */
	private void setAttribute() {
		player.attributes.set("OBELISK", -1);
		player.attributes.set("BANK_KEY", false);
		player.attributes.set("TRADE_KEY", false);
		player.attributes.set("RUN_FLAG_KEY", 10);
		player.attributes.set("PLAYER_TITLE_KEY", 0);
		player.attributes.set("FORCE_MOVEMENT", false);
		player.attributes.set("PRELOADING_SLOT_KEY", 0);
		player.attributes.set("PERSONAL_STORE_KEY", null);
		player.attributes.set("PRICE_CHECKER_KEY", false);
		player.attributes.set("DONATOR_DEPOSIT_KEY", false);
		player.attributes.set("TELEPORT_TYPE_KEY", TeleportType.FAVORITES);
		// player.attributes.set("TELEPORT_TYPE_KEY2", TeleportType.FAVORITES);
		
	}
	
	/**
	 * Sets the activity for the player.
	 */
	private void setActivity() {
		if(player.newPlayer) {
			TutorialActivity.create(player);
		} else if(player.punishment.isJailed()) {
			JailActivity.create(player);
		} else if(player.needsStarter) {
			StarterKit.open(player);
		} else if(Area.inWarriorGuild(player)) {
			WarriorGuild.create(player);
		} else if(Area.inBarrows(player)) {
			// Barrows.create(player);
		} else if(Area.inWilderness(player)) {
			player.valueIcon = getValueIcon(player).getCode();
			player.updateFlags.add(UpdateFlag.APPEARANCE);
		}
	}
	
	/**
	 * Sends the context menu to the {@code Player}'s context.
	 */
	private void setContextMenu() {
		if(Area.inDuelArenaLobby(player)) {
			player.send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false));
			disableAttack();
		} else if(Area.inWilderness(player) || Area.inEventArena(player)) {
			disableAttack();
			player.send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
		} else if(Area.inDuelArena(player) || Area.inDuelObsticleArena(player)) {
			disableAttack();
			player.send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
		} else {
			disableAttack();
			player.send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
		}
		player.send(new SendPlayerOption(PlayerOption.FOLLOW, false));
		player.send(new SendPlayerOption(PlayerOption.TRADE_REQUEST, false));
		player.send(new SendPlayerOption(PlayerOption.VIEW_PROFILE, false));
	}
	
	private void disableAttack() {
		if(player.brutalMode || Area.inBattleRealm(player)) {
			return;
		}
		player.send(new SendPlayerOption(PlayerOption.ATTACK, false, true));
	}
	
	/**
	 * Sets the prayer book.
	 */
	public void setPrayer() {
		Consumer<Prayer> enable = prayer -> {
			if(player.prayer.anyActive(Prayer.RETRIBUTION, Prayer.REDEMPTION, Prayer.SMITE)) {
				player.getCombat().addListener(PrayerListener.get());
			}
			player.send(new SendConfig(prayer.getConfig(), 1));
			prayer.getListener().ifPresent(listener -> player.getCombat().addListener(listener));
		};
		Consumer<Prayer> disable = prayer -> {
			if(prayer == Prayer.RETRIBUTION || prayer == Prayer.REDEMPTION || prayer == Prayer.SMITE)
				player.getCombat().removeListener(PrayerListener.get());
			player.send(new SendConfig(prayer.getConfig(), 0));
			prayer.getListener().ifPresent(listener -> player.getCombat().removeListener(listener));
		};
		Consumer<Prayer> overhead = prayer -> {
			player.headIcon = prayer.getHeadIcon();
			player.updateFlags.add(UpdateFlag.APPEARANCE);
		};
		Consumer<Prayer> noOverhead = prayer -> {
			player.headIcon = -1;
			player.updateFlags.add(UpdateFlag.APPEARANCE);
		};
		player.prayer.setOnChange(enable, disable, overhead, noOverhead);
		Consumer<Prayer> qEnable = prayer -> player.send(new SendConfig(prayer.getQConfig(), 0));
		Consumer<Prayer> qDisable = prayer -> player.send(new SendConfig(prayer.getQConfig(), 1));
		player.quickPrayers.setOnChange(qEnable, qDisable, Prayer::getButton, Prayer::getButton);
		Arrays.stream(Prayer.values()).forEach(prayer -> {
			disable.accept(prayer);
			
			if(player.quickPrayers.isActive(prayer)) {
				qEnable.accept(prayer);
			} else {
				qDisable.accept(prayer);
			}
		});
	}
	
	/**
	 * Resets the player's variables.
	 */
	public void reset() {
		resetEffects();
		setWidget();
		player.movement.reset();
		player.getCombat().reset();
		player.prayer.reset();
		player.headIcon = -1;
		player.updateFlags.add(UpdateFlag.APPEARANCE);
	}
	
	/**
	 * Reset's all the player's activated effects.
	 */
	private void resetEffects() {
		CombatUtil.cancelEffect(player, CombatEffectType.TELEBLOCK);
		CombatUtil.cancelEffect(player, CombatEffectType.ANTIFIRE_POTION);
		CombatUtil.cancelEffect(player, CombatEffectType.SKULL);
		CombatUtil.cancelEffect(player, CombatEffectType.VENOM);
	}
	
	/**
	 * Sends all the widget data to the client.
	 */
	private void setWidget() {
		for(WidgetType widget : WidgetType.values()) {
			player.send(new SendWidget(widget, 0));
		}
	}
	
	/**
	 * Handles the running.
	 */
	private void run() {
		if(player.movement.isRunning() && player.movement.isMoving() && !player.isBot && !PlayerRight.isDeveloper(player)) {
			if(!(player.staminaExpireTime > System.currentTimeMillis() && player.sequence % 4 == 0)) {
				player.runEnergy--;
			}
			
			if(player.runEnergy < 0)
				player.runEnergy = 0;
			if(player.runEnergy == 0)
				player.movement.setRunningToggled(false);
			player.send(new SendRunEnergy());
		}
	}
	
	/**
	 * Handles restoring the run energy.
	 */
	private void runRestore() {
		if(player.resting && player.sequence % 50 == 0) {
			player.animate(player.right.getRestAnimation());
		}
		if(player.runEnergy < 100) {
			int rate = player.energyRate > 0 ? 2 : player.resting ? 3 : 4;
			if(player.sequence % rate == 0) {
				player.runEnergy++;
				player.send(new SendRunEnergy());
			}
		}
		if(player.energyRate > 0) {
			player.energyRate--;
		}
	}
	
	/**
	 * Handles restoration of skills.
	 */
	private void skillRestore() {
		if(player.sequence % 120 != 0 && player.sequence % 50 != 0)
			return;
		if(player.sequence % 100 == 0) {
			player.skills.regress(Skill.HITPOINTS);
		} else if(player.sequence % 50 == 0 && player.prayer.isActive(Prayer.RAPID_HEAL)) {
			player.skills.regress(Skill.HITPOINTS);
		}
		for(int index = 0; index <= 6; index++) {
			if(index == Skill.HITPOINTS || index == Skill.PRAYER)
				continue;
			int amount = 100;
			Skill skill = player.skills.get(index);
			if(skill.getLevel() < skill.getMaxLevel() && player.prayer.isActive(Prayer.RAPID_RESTORE))
				amount = 50;
			if(skill.getLevel() > skill.getMaxLevel() && player.prayer.isActive(Prayer.PRESERVE))
				amount = 150;
			if(player.sequence % amount == 0)
				player.skills.regress(index);
		}
	}
	
	/**
	 * Handles draining prayer.
	 */
	private void prayerDrain() {
		if(!player.prayer.isActive()) {
			return;
		}
		int bonus = player.getBonus(Equipment.PRAYER_BONUS);
		int rate = player.prayer.drainAmount(bonus);
		drainPrayer(rate);
	}
	
	public void drainPrayer(int amount) {
		if(amount > 0) {
			Skill skill = player.skills.get(Skill.PRAYER);
			skill.modifyLevel(level -> level - amount, 0, skill.getLevel());
			player.skills.refresh(Skill.PRAYER);
			
			if(skill.getLevel() == 0) {
				player.send(new SendMessage("You have run out of prayer points; you must recharge at an altar."));
				player.prayer.reset();
				player.getPlayer().send(new SendConfig(659, 0));
			}
		}
	}
	
	public void claimIronmanArmour() {
		if(player.right == PlayerRight.IRONMAN) {
			player.inventory.addOrDrop(new Item(12810), new Item(12811), new Item(12812));
		} else if(player.right == PlayerRight.ULTIMATE_IRONMAN) {
			player.inventory.addOrDrop(new Item(12813), new Item(12814), new Item(12815));
		} else if(player.right == PlayerRight.HARDCORE_IRONMAN) {
			player.inventory.addOrDrop(new Item(20792), new Item(20794), new Item(20796));
		}
	}
	
	/**
	 * Copy's the inventory and equipment of another player.
	 */
	public void copy(Player other) {
		Item[] inventory = other.inventory.toArray();
		Item[] equipment = other.equipment.toArray();
		player.inventory.clear(false);
		player.equipment.clear(false);
		player.inventory.addAll(inventory);
		player.equipment.manualWearAll(equipment);
		player.inventory.refresh();
		player.equipment.login();
	}
	
	/**
	 * Tranforms player into mob.
	 */
	public void transform(int npc) {
		if(npc == -1) {
			player.actorAnimation.reset();
			player.setWidth(1);
			player.setLength(1);
		} else {
			MobDefinition def = MobDefinition.get(npc);
			if(def != null) {
				player.actorAnimation.setStand(def.getStand());
				player.actorAnimation.setWalk(def.getWalk());
				player.actorAnimation.setTurn180(def.getTurn180());
				player.actorAnimation.setTurn90CW(def.getTurn90CW());
				player.actorAnimation.setTurn90CCW(def.getTurn90CCW());
				player.setWidth(def.getSize());
				player.setLength(def.getSize());
			}
		}
		player.id = npc;
		player.updateFlags.add(UpdateFlag.APPEARANCE);
	}
	
	/**
	 * Handles displaying the welcome itemcontainer.
	 */
	void welcomeScreen() {
		boolean wants = player.settings.welcomeScreen;
		if(!wants || Area.inWilderness(player) || player.getCombat().inCombat() || player.newPlayer || player.needsStarter) {
			player.setVisible(true);
			player.send(new SendScreenMode(player.settings.clientWidth, player.settings.clientHeight));
			return;
		}
		if(player.settings.clientWidth > 765 || player.settings.clientWidth > 503) {
			player.settings.clientWidth = 765;
			player.settings.clientHeight = 503;
			player.send(new SendScreenMode(765, 503));
		}
		//		for (int index = 0; index < 3; index++) {
		player.send(new SendString(Config.WELCOME_DIALOGUE[0/*index*/], 21315 /*+ index*/));
		//		}
		player.send(new SendInterfaceAnimation(21310, Expression.HAPPY.getId()));
		player.send(new SendMarquee(21319, Config.WELCOME_MARQUEE));
		player.send(new SendString("You last logged in <col=EB4646>earlier today</col>.", 21318));// TODO
		// black marks
		player.send(new SendString("", 21320));
		player.send(new SendString("You have 0 black marks!", 21321));
		player.send(new SendString("Keep up the good work!", 21322));
		player.send(new SendString("", 21323));
		// bank pins
		player.send(new SendString("You do not have a bank", 21324));
		player.send(new SendString("pin set! Speak to any", 21325));
		player.send(new SendString("banker to set one.", 21326));
		player.send(new SendString("Bank pins are security!", 21327));
		// other
		player.send(new SendString("Click to vote", 21328));
		player.send(new SendString("Click to donate", 21329));
		player.send(new SendString("Click change e-mail", 21330));
		player.send(new SendString("Click to change pass", 21331));
		// announcement
		player.send(new SendString(Config.WELCOME_ANNOUNCEMENT[0], 21332));
		player.send(new SendString(Config.WELCOME_ANNOUNCEMENT[1], 21333));
		player.send(new SendString(Config.WELCOME_ANNOUNCEMENT[2], 21334));
		player.send(new SendString(Config.WELCOME_ANNOUNCEMENT[3], 21335));
		player.send(new SendString(Config.WELCOME_ANNOUNCEMENT[4], 21336));
		// update
		player.send(new SendString(Config.WELCOME_UPDATE[0], 21337));
		player.send(new SendString(Config.WELCOME_UPDATE[1], 21338));
		player.send(new SendString(Config.WELCOME_UPDATE[2], 21339));
		player.send(new SendString(Config.WELCOME_UPDATE[3], 21340));
		player.send(new SendString(Config.WELCOME_UPDATE[4], 21341));
		player.interfaceManager.open(450);
	}
	
	/** @author Ferooz Hiddar */
	/**
	 * Handles sending the destroy item dialogue.
	 */
	public void destroyItem(Item item, int slot) {
		player.send(new SendItemOnInterfaceSlot(14171, item, 0));
		player.send(new SendString("Are you sure you want to destroy this item?", 14174));
		player.send(new SendString("Yes.", 14175));
		player.send(new SendString("No.", 14176));
		player.send(new SendString("", 14177));
		player.send(new SendString(/* item.getDestroyMessage() */"Doing this action is permanent!", 14182));
		player.send(new SendString("", 14183));
		player.send(new SendString(item.getName(), 14184));
		player.send(new SendChatBoxInterface(14170));
		player.attributes.set("DESTROY_ITEM_KEY", slot);
	}
	
	/**
	 * Handles destroying the item.
	 */
	public void handleDestroyItem() {
		if(player.attributes.has("UNCHARGE_HELM_KEY")) {
			Item item = player.attributes.get("UNCHARGE_HELM_KEY");
			player.dialogueFactory.clear();
			player.inventory.replace(item, new Item(12_934, 20_000), true);
			player.send(new SendMessage("You have dismantled your " + item.getName() + "."));
			player.attributes.remove("UNCHARGE_HELM_KEY");
			return;
		}
		
		if(player.attributes.has("RESTORE_HELM_KEY")) {
			Item item = player.attributes.get("RESTORE_HELM_KEY");
			player.dialogueFactory.clear();
			if(item.matchesId(13_197)) {
				player.serpentineHelmCharges += player.tanzaniteHelmCharges;
				player.tanzaniteHelmCharges = 0;
				if(player.serpentineHelmCharges > 11_000) {
					player.inventory.addOrDrop(new Item(12934, player.serpentineHelmCharges - 11_000));
					player.serpentineHelmCharges = 11_000;
				}
				player.inventory.replace(item, new Item(player.serpentineHelmCharges <= 0 ? 12_929 : 12_931), true);
			} else if(item.matchesId(13_199)) {
				player.serpentineHelmCharges += player.magmaHelmCharges;
				player.magmaHelmCharges = 0;
				if(player.serpentineHelmCharges > 11_000) {
					player.inventory.addOrDrop(new Item(12934, player.serpentineHelmCharges - 11_000));
					player.serpentineHelmCharges = 11_000;
				}
			}
			player.inventory.replace(item, new Item(player.serpentineHelmCharges <= 0 ? 12_929 : 12_931), true);
			player.send(new SendMessage("You have restored your " + item.getName() + "."));
			player.attributes.remove("RESTORE_HELM_KEY");
			return;
		}
		
		int index = player.attributes.get("DESTROY_ITEM_KEY", Integer.class);
		if(index == -1)
			return;
		Item item = player.inventory.get(index);
		if(item == null)
			return;
		player.dialogueFactory.clear();
		player.inventory.remove(item, index);
		player.send(new SendMessage("You have destroyed your " + item.getName() + "."));
		player.attributes.remove("DESTROY_ITEM_KEY");
	}
	
	public boolean contains(Item item) {
		return player.inventory.contains(item) || player.equipment.contains(item) || player.bank.contains(item);
	}
	
	/**
	 * Gets the KDR of the player.
	 */
	public String kdr() {
		double KDR = (player.kill / (double) player.death);
		
		return Double.isNaN(KDR) ? "0.0" : String.format("%.2f", KDR);
	}
	
	/**
	 * Gets the max hit of a combat type.
	 */
	public int getMaxHit(Actor defender, CombatType type) {
		int max = FormulaFactory.getModifiedMaxHit(player, defender, type);
		player.getCombat().addFirst(player.getStrategy());
		max = player.getCombat().modifyDamage(player, max);
		player.getCombat().removeFirst();
		return max;
	}
	
	/**
	 * Gets the instanced height for player.
	 */
	public int instance() {
		return player.getIndex() << 2;
	}
	
	/**
	 * Gets the total amount of 99s the player has.
	 */
	public int getMaxSkillCount() {
		int count = 0;
		for(int index = 0; index < Skill.SKILL_COUNT; index++) {
			if(player.skills.getMaxLevel(index) == 99)
				count++;
		}
		return count;
	}
	
	public long networth() {
		return networth(player.inventory, player.equipment, player.bank);
	}
	
	/**
	 * Gets the net worth of the player.
	 */
	public long networth(ItemContainer... containers) {
		long networth = 0;
		for(ItemContainer container : containers) {
			networth += container.containerValue(PriceType.VALUE);
		}
		return networth;
	}
	
	/**
	 * Gets the total carried weight of the player.
	 */
	public double weight() {
		double weight = player.inventory.getWeight();
		weight += player.equipment.getWeight();
		return weight;
	}
	
	public ValueIcon getValueIcon(Player player) {
		ValueIcon icon = ValueIcon.NONE;
		long carrying = networth(player.inventory, player.equipment);
		
		if(carrying < 500_000)
			icon = ValueIcon.BRONZE;
		if(carrying > 500_000 && carrying < 1_500_000)
			icon = ValueIcon.SILVER;
		if(carrying > 1_500_000 && carrying < 5_000_000)
			icon = ValueIcon.GREEN;
		if(carrying > 5_000_000 && carrying < 25_000_000)
			icon = ValueIcon.BLUE;
		if(carrying > 25_000_000)
			icon = ValueIcon.RED;
		
		return icon;
	}
	
	/**
	 * Checks if player is busy.
	 */
	public boolean busy() {
		return !player.interfaceManager.isMainClear() || player.isDead() || player.newPlayer || player.needsStarter || player.locking.locked() || player.inActivity(ActivityType.DUEL_ARENA);
	}
	
	/**
	 * Checks if the string is already stored in the list.
	 */
	public boolean checkSendString(String text, int id) {
		if(!interfaceText.containsKey(id)) {
			interfaceText.put(id, new TinterfaceText(text, id));
			return true;
		}
		TinterfaceText t = interfaceText.get(id);
		if(t.currentState.equals(text))
			return false;
		t.currentState = text;
		return true;
	}
	
	/**
	 * Clears the send strings
	 */
	public void clearSendStrings() {
		interfaceText.clear();
	}
}
