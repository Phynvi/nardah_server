package com.nardah.game.world.entity.actor.player;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.activity.Activity;
import com.nardah.content.bot.BotUtility;
import com.nardah.content.bot.PlayerBot;
import com.nardah.content.event.EventDispatcher;
import com.nardah.content.event.impl.OnKillEvent;
import com.nardah.content.pet.Pets;
import com.nardah.content.writer.InterfaceWriter;
import com.nardah.content.writer.impl.InformationWriter;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.listener.CombatListenerManager;
import com.nardah.game.world.entity.combat.effect.CombatEffectType;
import com.nardah.game.world.entity.combat.strategy.player.special.CombatSpecial;
import com.nardah.game.world.position.Area;
import com.nardah.util.Utility;
import com.nardah.Config;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.ActorDeath;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendRunEnergy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the player death listener.
 * @author Daniel
 */
public final class PlayerDeath extends ActorDeath<Player> {
	
	private boolean safe = false;
	
	/**
	 * Array of all death messages.
	 */
	private static final String[] DEATH_MESSAGES = {"You have defeated $VICTIM.", "With a crushing blow, you defeat $VICTIM.", "It's a humiliating defeat for $VICTIM.", "$VICTIM didn't stand a chance against you.", "You have defeated $VICTIM.", "It's all over for $VICTIM.", "$VICTIM regrets the day they met you in combat.", "$VICTIM falls before your might.", "Can anyone defeat you? Certainly not $VICTIM.", "You were clearly a better fighter than $VICTIM."};
	
	/**
	 * Creates a new {@link ActorDeath}.
	 */
	PlayerDeath(Player mob) {
		super(mob, 2);
	}
	
	/**
	 * The part of the death process where the character is prepared for the rest of
	 * the death process.
	 */
	@Override
	public void preDeath(Actor killer) {
		mob.animate(new Animation(836, UpdatePriority.VERY_HIGH));
	}
	
	/**
	 * The main part of the death process where the killer is found for the
	 * character.
	 */
	@Override
	public void death() {
		Actor killer = mob.getCombat().getDamageCache().calculateProperKiller().orElse(null);
		
		if(mob.inActivity() && Activity.evaluate(mob, Activity::safe)) {
			safe = true;
			return;
		}
		
		if(Area.inZulrah(mob)) {
			safe = true;
			return;
		}
		
		if(Area.inEventArena(mob)) {
			safe = true;
			mob.equipment.unequip(Equipment.ARROWS_SLOT);
			mob.equipment.unequip(Equipment.AMULET_SLOT);
			mob.equipment.unequip(Equipment.HEAD_SLOT);
			mob.equipment.unequip(Equipment.HANDS_SLOT);
			mob.equipment.unequip(Equipment.LEGS_SLOT);
			mob.equipment.unequip(Equipment.FEET_SLOT);
			mob.equipment.unequip(Equipment.CHEST_SLOT);
			mob.equipment.unequip(Equipment.WEAPON_SLOT);
			mob.equipment.unequip(Equipment.SHIELD_SLOT);
			mob.equipment.unequip(Equipment.RING_SLOT);
			mob.equipment.unequip(Equipment.CAPE_SLOT);
			
			mob.send(new SendMessage("@red@Your Items have either been banked or sent to your inventory."));
			return;
		}
		
		if(!PlayerRight.isPriviledged(mob)) {
			Pets.onDeath(mob);
			calculateDropItems(mob, killer);
		}
		
		if(killer == null)
			return;
		
		switch(killer.getType()) {
			case PLAYER:
				Player playerKiller = killer.getPlayer();
				
				if(mob.inventory.contains(11941)) {
					mob.lootingBag.clear();
					System.out.println("[LOOTING BAG] " + mob.getName() + " has just dropped/lost his looting bag");
				}
				
				if(mob.isBot) {
					
					playerKiller.message("<col=295EFF>You were rewarded with 1 point for that bot kill. You now have: " + Utility.formatDigits(playerKiller.pkPoints) + ".");
					return;
				}
				
				if(!PlayerKilling.contains(playerKiller, mob.lastHost)) {
					AchievementHandler.activate(playerKiller, AchievementKey.KILLER, 1);
					// DailyAchievementHandler.activate(playerKiller, DailyAchievementKey.KILLER,
					// 1);
					// DailyAchievementHandler.activate(playerKiller,
					// DailyAchievementKey.KILL_SKOTIZO, 1);
					
					playerKiller.send(new SendMessage(Utility.randomElement(DEATH_MESSAGES).replace("$VICTIM", mob.getName())));
					PlayerKilling.handle(playerKiller, mob);
				} else {
					playerKiller.message("<col=FF0019>You have recently killed " + mob.getName() + " and therefore were not rewarded. You must kill ", "<col=FF0019>3 new players to reset this!");
				}
				
				EventDispatcher.execute(playerKiller, new OnKillEvent(mob));
				break;
			case NPC:
				break;
			default:
				break;
		}
		
	}
	
	/**
	 * The last part of the death process where the character is reset.
	 */
	@Override
	public void postDeath(Actor killer) {
		
		if(mob.isBot) {
			((PlayerBot) mob).postDeath();
			return;
		}
		
		mob.unpoison();
		CombatUtil.cancelEffect(mob, CombatEffectType.POISON);
		CombatUtil.cancelEffect(mob, CombatEffectType.VENOM);
		
		mob.runEnergy = 100;
		mob.skulling.unskull();
		mob.skills.restoreAll();
		mob.inventory.refresh();
		// actor.equipment.login();
		mob.action.reset();
		mob.playerAssistant.reset();
		mob.interfaceManager.close();
		mob.setSpecialActivated(false);
		mob.getCombat().getDamageCache().clear();
		mob.send(new SendRunEnergy());
		CombatSpecial.restore(mob, 100);
		mob.movement.reset();
		mob.teleblockTimer.set(0);
		mob.equipment.updateAnimation();
		// actor.equipment.refresh();
		mob.equipment.login();
		
		// IF YOU'RE HAVING AN ISSUE WITH ITEM LISTENERS AFTER DEATH, IT'S THIS FUNCTION
		// RIGHT HERE
		// This function was implementing because dying with a full set of barrows let
		// you keep the effect.
		CombatListenerManager.removeAllPlayerListeners(mob);
		
		if(mob.inActivity()) {
			Activity.forActivity(mob, it -> it.onDeath(mob));
			//            return;
		}
		
		mob.move(Config.DEFAULT_POSITION);
		mob.send(new SendMessage("Oh dear, you are dead!"));
		mob.animate(new Animation(-1, UpdatePriority.VERY_HIGH));
		
		if(mob.presetManager.deathOpen) {
			World.schedule(1, mob.presetManager::open);
		}
		
		if(!safe) {
			if(killer != null && killer.isPlayer() && !mob.equals(killer)) {
				mob.killstreak.end(killer.getName());
			}
			if(mob.right == PlayerRight.HARDCORE_IRONMAN) {
				mob.right = PlayerRight.IRONMAN;
				mob.updateFlags.add(UpdateFlag.APPEARANCE);
				mob.send(new SendMessage("You have lost your hardcore iron man status since you died!!!"));
				World.sendMessage("<col=FF0000>" + Config.SERVER_NAME + "<col=" + mob.right.getColor() + ">" + mob.getName() + "</col>'s " + (mob.brutalMode ? " Brutal Mode " : "") + "hardcore iron man account was lost!");
			}
		}
		
		InterfaceWriter.write(new InformationWriter(mob));
		
		mob.animate(new Animation(-1, UpdatePriority.VERY_HIGH));
		
	}
	
	/**
	 * Calculates and drops all of the items from {@code character} for
	 * {@code killer}.
	 */
	private void calculateDropItems(Player character, Actor killer) {
		Player theKiller = killer == null || killer.isNpc() ? character : killer.getPlayer();
		
		if(character.right.equals(PlayerRight.ULTIMATE_IRONMAN)) {
			List<Item> items = new LinkedList<>();
			character.equipment.forEach(items::add);
			character.inventory.forEach(items::add);
			character.lootingBag.forEach(items::add);
			character.equipment.clear();
			character.inventory.clear();
			character.lootingBag.clear();
			items.forEach(item -> {
				if(!item.isTradeable()) {
					if(!character.lostUntradeables.deposit(item)) {
						GroundItem.create(character, item);
					}
				} else {
					GroundItem.create(theKiller, item, character.getPosition());
				}
			});
			return;
		}
		
		LinkedList<Item> toDrop = new LinkedList<>();
		List<Item> keep = new LinkedList<>();
		List<Item> items = new LinkedList<>();
		character.equipment.forEach(items::add);
		Item[] lootingBag = character.lootingBag.getDeathItems();
		character.inventory.forEach(item -> {
			if(item.getId() != 11941) {
				items.add(item);
			}
		});
		character.equipment.clear();
		character.inventory.clear();
		
		if(lootingBag != null) {
			items.addAll(Arrays.asList(lootingBag));
			character.lootingBag.clear();
		}
		
		toDrop.addAll(items);
		
		toDrop.sort((first, second) -> second.getValue() - first.getValue());
		
		if(!character.skulling.isSkulled()) {
			keep.add(toDrop.pollFirst());
			keep.add(toDrop.pollFirst());
			keep.add(toDrop.pollFirst());
		}
		
		if(character.prayer.isActive(Prayer.PROTECT_ITEM)) {
			keep.add(toDrop.pollFirst());
		}
		
		keep.forEach(item -> {
			if(item == null) {
				return;
			}
			
			character.inventory.add(new Item(item.getId()));
			if(item.isStackable() && item.getAmount() > 1) {
				toDrop.add(item.createAndDecrement(1));
			}
		});
		
		if(theKiller.isBot) {
			toDrop.forEach(item -> {
				if(character.runecraftPouch.death(item))
					return;
				
				if(character.runePouch.death(item))
					return;
				
				if(!item.isTradeable()) {
					if(!character.lostUntradeables.deposit(item)) {
						GroundItem.create(character, item);
					}
					return;
				}
				
				if(theKiller.isBot && item.getValue() >= 50_000) {
					return;
				}
				
				BotUtility.logLoot(item);
			});
			
			GroundItem drop = GroundItem.create(theKiller, new Item(526), character.getPosition());
			if(!theKiller.equals(character) && PlayerRight.isIronman(theKiller)) {
				drop.canIronMenPickThisItemUp = false;
			}
			return;
		}
		
		toDrop.forEach(item -> {
			if(character.runecraftPouch.death(item))
				return;
			
			if(character.runePouch.death(item))
				return;
			
			if(!item.isTradeable()) {
				if(!character.lostUntradeables.deposit(item)) {
					GroundItem.create(character, item);
				}
				return;
			}
			
			if(theKiller.isBot && item.getValue() >= 50_000) {
				return;
			}
			
			GroundItem drop = GroundItem.create(theKiller, item, character.getPosition());
			if(!theKiller.equals(character) && PlayerRight.isIronman(theKiller)) {
				drop.canIronMenPickThisItemUp = false;
			}
		});
		
		GroundItem drop = GroundItem.create(theKiller, new Item(526), character.getPosition());
		if(!theKiller.equals(character) && PlayerRight.isIronman(theKiller)) {
			drop.canIronMenPickThisItemUp = false;
		}
	}
	
}