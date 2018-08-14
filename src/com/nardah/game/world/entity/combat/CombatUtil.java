package com.nardah.game.world.entity.combat;

import com.nardah.content.activity.ActivityType;
import com.nardah.content.activity.impl.battlerealm.BattleRealmCallers;
import com.nardah.content.skill.impl.slayer.SlayerTask;
import com.nardah.game.world.entity.combat.effect.AntifireDetails;
import com.nardah.game.world.entity.combat.effect.CombatEffect;
import com.nardah.game.world.entity.combat.effect.CombatEffectType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.HitIcon;
import com.nardah.game.world.entity.combat.hit.Hitsplat;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.position.Area;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.net.packet.out.SendMessage;

import java.util.List;
import java.util.function.Consumer;

/**
 * A collection of util methods and constants related to combat.
 * @author lare96 <http://github.com/lare96>
 */
public final class CombatUtil {
	
	/**
	 * The default constructor.
	 * @throws UnsupportedOperationException if this class is instantiated.
	 */
	private CombatUtil() {
		throw new UnsupportedOperationException("This class cannot be instantiated!");
	}
	
	/**
	 * Executes an action for mobs within a 3x3 square, including the source
	 * {@code actor}.
	 * @param actor the actor to generate an area for
	 * @param action the action to apply to all mobs in the area
	 */
	public static void areaAction(Actor actor, Consumer<Actor> action) {
		action.accept(actor);
		areaAction(actor, 3 * 3, 1, action);
	}
	
	/**
	 * Sends an action to {@link Actor} instance which is within a {@code
	 * distance}.
	 * @param action action consumer.
	 */
	public static void areaAction(Actor actor, int max, int distance, Consumer<Actor> action) {
		if(!Area.inMulti(actor)) {
			return;
		}
		
		int added = 0;
		List<Player> players = World.getRegions().getLocalPlayers(actor);
		players.sort((first, second) -> {
			int firstD = Utility.getDistance(first, actor);
			int secondD = Utility.getDistance(second, actor);
			return firstD - secondD;
		});
		
		for(Player other : players) {
			if(other == null)
				continue;
			if(other.instance != actor.instance)
				continue;
			if(!Utility.withinViewingDistance(other, actor, distance))
				continue;
			if(other.equals(actor))
				continue;
			if(other.getCurrentHealth() <= 0 || other.isDead())
				continue;
			if(!Area.inMulti(other))
				continue;
			if(actor.isPlayer() && other.isPlayer() && (!Area.inWilderness(actor) || !Area.inWilderness(other)))
				continue;
			action.accept(other);
			if(++added == max)
				return;
		}
		
		List<Mob> mobs = World.getRegions().getLocalNpcs(actor);
		mobs.sort((first, second) -> {
			int firstD = Utility.getDistance(first, actor);
			int secondD = Utility.getDistance(second, actor);
			return firstD - secondD;
		});
		
		for(Mob other : mobs) {
			if(other == null)
				continue;
			if(other.instance != actor.instance)
				continue;
			if(!Utility.withinViewingDistance(other, actor, distance))
				continue;
			if(other.equals(actor))
				continue;
			if(other.getCurrentHealth() <= 0 || other.isDead())
				continue;
			if(!Area.inMulti(other))
				continue;
			if(!other.definition.isAttackable())
				continue;
			action.accept(other);
			if(++added == max)
				return;
		}
	}
	
	/**
	 * Gets the hit delay for the specified {@code type}.
	 * @param attacker the character doing the hit
	 * @param defender the victim being hit
	 * @param type the combat type of this hit
	 * @return the delay for the combat type
	 */
	public static int getHitDelay(Actor attacker, Actor defender, CombatType type) {
		if(!type.equals(CombatType.MELEE)) {
			int distance = Utility.getDistance(attacker, defender);
			
			if(distance > 10) {
				distance = 10;
			}
			
			if(type.equals(CombatType.MAGIC)) {
				return Projectile.MAGIC_DELAYS[distance] - 1;
			}
			
			if(type.equals(CombatType.RANGED)) {
				return Projectile.RANGED_DELAYS[distance] - 1;
			}
		}
		
		/* Otherwise, melee */
		return 0;
	}
	
	/**
	 * Gets the hitsplat delay for the specified {@code type}.
	 * @param type the combat type of this hit
	 * @return the delay for the combat type
	 */
	public static int getHitsplatDelay(CombatType type) {
		if(!type.equals(CombatType.MELEE)) {
			return 1;
		}
		
		return 0;
	}
	
	static boolean validateMobs(Actor attacker, Actor defender) {
		if(!validate(attacker) || !validate(defender)) {
			attacker.getCombat().reset();
			return false;
		}
		
		if(attacker.instance != defender.instance) {
			attacker.getCombat().reset();
			return false;
		}
		
		if(!canAttack(attacker, defender)) {
			attacker.getCombat().reset();
			return false;
		}
		return true;
	}
	
	/**
	 * Applies the {@code effect} in any context.
	 * @param effect the effect that must be applied
	 * @return {@code true} if it was successfully applied
	 */
	public static boolean effect(Actor actor, CombatEffectType effect) {
		return CombatEffect.EFFECTS.get(effect).start(actor);
	}
	
	/**
	 * Cancels the {@code effect} in any context.
	 * @param effect the effect that must be applied
	 * @return {@code true} if it was successfully applied
	 */
	public static boolean cancelEffect(Actor actor, CombatEffectType effect) {
		return CombatEffect.EFFECTS.get(effect).removeOn(actor);
	}
	
	public static boolean canAttack(Actor attacker, Actor defender) {
		if(attacker.isPlayer())
			return canAttack(attacker.getPlayer(), defender);
		return canAttack(attacker.getNpc(), defender);
	}
	
	private static boolean canAttack(Player attacker, Actor defender) {
		if(defender.isNpc() && !SlayerTask.canAttack(attacker, defender.id)) {
			attacker.send(new SendMessage("You do not meet the slayer requirements to attack this mob!"));
			return false;
		}
		if(attacker.equals(defender)) {
			attacker.send(new SendMessage("Okay low-key, you're not supposed to be able to attack yourself."));
			attacker.send(new SendMessage("But as a developer (Red Bracket), I'm super curious to know how"));
			attacker.send(new SendMessage("you pulled this off. I'm gonna let you do it, but please don't"));
			attacker.send(new SendMessage("break anything :)"));
			World.sendMessage(attacker.getName() + " is potentially bug abusing. Someone go check him out!");
			// attacker.send(new SendMessage("You can't attack yourself!"));
			// return false;
			return true;
		}
		
		if(defender.isNpc() && defender.getNpc().owner != null && !attacker.equals(defender.getNpc().owner)) {
			attacker.send(new SendMessage("You can't attack this mob!"));
			return false;
		}
		
		if(attacker.brutalMode && defender.brutalMode) {
			return true;
		}
		if(attacker.getCombat().isUnderAttack() && !attacker.getCombat().isUnderAttackBy(defender)) {
			if(!Area.inMulti(attacker) || !Area.inMulti(defender)) {
				attacker.send(new SendMessage("You are already under attack!"));
				return false;
			}
		}
		
		if(defender.getCombat().isUnderAttack() && !defender.getCombat().isUnderAttackBy(attacker)) {
			if(!Area.inMulti(attacker) || !Area.inMulti(defender)) {
				if(defender.isPlayer()) {
					attacker.send(new SendMessage(defender.getName() + " is currently in combat and can not be attacked."));
				} else {
					attacker.send(new SendMessage("This monster is already under attack!"));
				}
				return false;
			}
		}
		if(defender.isPlayer()) {
			if(attacker.locking.locked(PacketType.COMBAT)) {
				return false;
			}
			
			if(attacker.inActivity(ActivityType.DUEL_ARENA) && defender.inActivity(ActivityType.DUEL_ARENA)) {
				return true;
			}
			
			// BattleRealm Stuff
			if(BattleRealmCallers.canFight(attacker, defender)) {
				return true;
			} else if(BattleRealmCallers.cantFight(attacker, defender)) {
				attacker.message("You cannot kill your own team!");
				return false;
			}
			
			if(Area.inEventArena(attacker) && Area.inEventArena(defender)) {
				return true;
			}
			
			int difference = (int) Math.abs(attacker.skills.getCombatLevel() - defender.skills.getCombatLevel());
			
			if(attacker.brutalMode && !defender.brutalMode) {
				attacker.send(new SendMessage("Sorry, " + defender.getName() + " is not a Brutal Man."));
				return false;
			}
			
			if(difference > attacker.wilderness && Area.inWilderness(attacker)) {
				attacker.send(new SendMessage("Your combat level difference is too great!"));
				return false;
			}
			
			if(!Area.inWilderness(attacker)) {
				attacker.send(new SendMessage("You need to be in the wilderness to attack " + Utility.formatName(defender.getName()) + "."));
				return false;
			}
			
			if(!Area.inWilderness(defender)) {
				attacker.send(new SendMessage(Utility.formatName(defender.getName()) + " must be in the wilderness for you to attack."));
				return false;
			}
			
		}
		return true;
	}
	
	private static boolean canAttack(Mob attacker, Actor defender) {
		if(attacker.equals(defender)) {
			return false;
		}
		if(defender.isNpc() && defender.getNpc().owner != null && !defender.getNpc().owner.equals(attacker)) {
			return false;
		}
		if(attacker.getCombat().isUnderAttack() && !attacker.getCombat().isUnderAttackBy(defender)) {
			if(!Area.inMulti(attacker) || !Area.inMulti(defender)) {
				return false;
			}
		}
		if(defender.getCombat().isUnderAttack() && !defender.getCombat().isUnderAttackBy(attacker)) {
			if(!Area.inMulti(attacker) || !Area.inMulti(defender)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean canBasicAttack(Actor attacker, Actor defender) {
		if(attacker.equals(defender)) {
			return false;
		}
		if(defender.isNpc() && defender.getNpc().owner != null && !attacker.equals(defender.getNpc().owner)) {
			return false;
		}
		if(attacker.getCombat().isUnderAttack() && !attacker.getCombat().isUnderAttackBy(defender)) {
			if(!Area.inMulti(attacker) || !Area.inMulti(defender)) {
				return false;
			}
		}
		if(defender.getCombat().isUnderAttack() && !defender.getCombat().isUnderAttackBy(attacker)) {
			if(!Area.inMulti(attacker) || !Area.inMulti(defender)) {
				return false;
			}
		}
		if(attacker.isPlayer() && defender.isPlayer()) {
			int difference = (int) Math.abs(attacker.skills.getCombatLevel() - defender.skills.getCombatLevel());
			if(difference > attacker.getPlayer().wilderness) {
				return false;
			}
			if(!Area.inWilderness(attacker)) {
				return false;
			}
			if(!Area.inWilderness(defender)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean validate(Actor actor) {
		return actor != null && !actor.isDead() && actor.isVisible() && actor.isValid() && !actor.teleporting && !actor.inTeleport;
	}
	
	static Animation getBlockAnimation(Actor actor) {
		int animation = 404;
		if(actor.isPlayer()) {
			if(actor.getPlayer().equipment.hasShield()) {
				Item shield = actor.getPlayer().equipment.getShield();
				animation = shield.getBlockAnimation().orElse(424);
			} else if(actor.getPlayer().equipment.hasWeapon()) {
				animation = 404;// TODO
			}
		} else {
			Mob mob = actor.getNpc();
			animation = mob.definition.getBlockAnimation();
		}
		int delay = (int) actor.getCombat().lastBlocked.elapsedTime();
		if(delay < 600)
			return new Animation(animation, delay / 50, UpdatePriority.LOW);
		return new Animation(animation, UpdatePriority.LOW);
	}
	
	public static CombatHit generateDragonfire(Actor attacker, Actor defender, int max, boolean prayer) {
		int hitDelay = getHitDelay(attacker, defender, CombatType.MAGIC);
		int hitsplatDelay = 1;
		return generateDragonfire(attacker, defender, max, hitDelay, hitsplatDelay, prayer);
	}
	
	public static CombatHit generateDragonfire(Actor attacker, Actor defender, int max, int hitDelay, int hitsplatDelay, boolean prayer) {
		int damage;
		
		if(defender.isPlayer()) {
			Player player = defender.getPlayer();
			
			if(Equipment.isWearingDFS(player) && player.dragonfireCharges < 50 && Utility.random(1, 8) == 2) {
				if(player.equipment.getShield().getId() == 11284) {
					player.equipment.set(Equipment.SHIELD_SLOT, new Item(11283), true);
					player.equipment.refresh();
				}
				player.animate(6695);
				player.graphic(1164);
				player.dragonfireCharges++;
				player.getCombat().setCooldown(5);
				player.send(new SendMessage("Your dragonfire Shield Absorbs the Dragon breath."));
				player.face(attacker);
				damage = 0;
			} else {
				prayer &= player.prayer.isActive(Prayer.PROTECT_FROM_MAGIC);
				boolean shield = player.equipment.containsAny(1540, 11283);
				boolean potion = player.getAntifireDetails().isPresent();
				
				if(shield && potion) {
					max = 0;
				} else if(potion) {
					AntifireDetails.AntifireType type = player.getAntifireDetails().get().getType();
					max -= type.getReduction();
					if(max <= 0) {
						max = 0;
					}
				} else if(shield) {
					max -= 50;
				} else if(prayer) {
					max -= 45;
				}
				
				damage = max == 0 ? 0 : RandomUtils.inclusive(max);
				if(damage >= 15) {
					player.send(new SendMessage("You are horribly burned by the dragonfire!"));
				} else if(!shield && !potion && !prayer && damage < 9 && damage > 0) {
					player.send(new SendMessage("You manage to resist some of the dragonfire."));
				}
			}
		} else {
			damage = max == 0 ? 0 : RandomUtils.inclusive(max);
		}
		
		Hit hit = new Hit(damage, Hitsplat.NORMAL, HitIcon.NONE, true);
		return new CombatHit(hit, hitDelay, hitsplatDelay);
	}
	
	public static CombatStrategy<Mob> randomStrategy(CombatStrategy<Mob>[] strategies) {
		return RandomUtils.random(strategies);
	}
	
	@SafeVarargs
	public static CombatStrategy<Mob>[] createStrategyArray(CombatStrategy<Mob>... strategies) {
		return strategies;
	}
	
}
