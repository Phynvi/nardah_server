package com.nardah.game.world.entity.combat;

import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.util.Stopwatch;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A fs of players who have inflicted damage on another player in a combat
 * session.
 * @author lare96 <http://github.com/lare96>
 */
public final class CombatDamage {
	
	/**
	 * The damages of players who have inflicted damage.
	 */
	private final Map<Actor, DamageCounter> attackers = new HashMap<>();
	
	public Hit lastHit;
	
	/**
	 * Registers damage in the backing collection for {@code character}. This method
	 * has no effect if the character isn't a {@code PLAYER} or if {@code amount} is
	 * below {@code 0}.
	 * @param character the character to register damage for.
	 * @param hit the hit to register.
	 */
	public void add(Actor character, Hit hit) {
		if(hit.getDamage() > 0) {
			DamageCounter counter = attackers.putIfAbsent(character, new DamageCounter(hit.getDamage()));
			if(counter != null)
				counter.incrementAmount(hit.getDamage());
			lastHit = hit;
		}
	}
	
	/**
	 * Determines which player in the backing collection has inflicted the most
	 * damage.
	 * @return the player who has inflicted the most damage, or an empty optional if
	 * there are no entries.
	 */
	public Optional<Mob> getNpcKiller() {
		int amount = 0;
		Mob killer = null;
		for(Map.Entry<Actor, DamageCounter> entry : attackers.entrySet()) {
			DamageCounter counter = entry.getValue();
			Actor entity = entry.getKey();
			
			if(!entity.isNpc() || entity.isDead() || !entity.isValid() || counter.isTimeout())
				continue;
			if(counter.getAmount() > amount) {
				amount = counter.getAmount();
				killer = entity.getNpc();
			}
		}
		return Optional.ofNullable(killer);
	}
	
	/**
	 * Determines which player in the backing collection has inflicted the most
	 * damage.
	 * @return the player who has inflicted the most damage, or an empty optional if
	 * there are no entries.
	 */
	public Optional<Player> getPlayerKiller() {
		int amount = 0;
		Player killer = null;
		for(Map.Entry<Actor, DamageCounter> entry : attackers.entrySet()) {
			DamageCounter counter = entry.getValue();
			Actor entity = entry.getKey();
			
			if(!entity.isPlayer() || entity.isDead() || entity.isValid() || counter.isTimeout())
				continue;
			if(counter.getAmount() > amount) {
				amount = counter.getAmount();
				killer = entity.getPlayer();
			}
		}
		return Optional.ofNullable(killer);
	}
	
	/**
	 * Determines which entity in the backing collection has inflicted the most
	 * damage.
	 * @return the player who has inflicted the most damage, or an empty optional if
	 * there are no entries.
	 */
	public Optional<Actor> calculateProperKiller() {
		int amount = 0;
		Actor killer = null;
		for(Map.Entry<Actor, DamageCounter> entry : attackers.entrySet()) {
			DamageCounter counter = entry.getValue();
			Actor actor = entry.getKey();
			
			if(actor.isDead() || !actor.isValid() || counter.isTimeout())
				continue;
			
			if(attackers.size() > 1 && actor.isPlayer() && PlayerRight.isIronman(actor.getPlayer()))
				continue;
			
			if(counter.getAmount() > amount) {
				amount = counter.getAmount();
				killer = actor;
			}
		}
		return Optional.ofNullable(killer);
	}
	
	/**
	 * Clears all data from the backing collection.
	 */
	public void clear() {
		attackers.clear();
	}
	
	/**
	 * A counter that will track the amount of damage dealt and whether that damaged
	 * has timed out or not.
	 * @author lare96 <http://github.com/lare96>
	 */
	private static final class DamageCounter {
		
		/**
		 * The amount of damage within this counter.
		 */
		private int amount;
		
		/**
		 * The stopwatch that will determine when a timeout occurs.
		 */
		private final Stopwatch stopwatch = Stopwatch.start();
		
		/**
		 * Creates a new {@link DamageCounter}.
		 * @param amount the amount of damage within this counter.
		 */
		public DamageCounter(int amount) {
			this.amount = amount;
		}
		
		/**
		 * Gets the amount of damage within this counter.
		 * @return the amount of damage.
		 */
		public int getAmount() {
			return amount;
		}
		
		/**
		 * Increments the amount of damage within this counter.
		 * @param amount the amount to increment by.
		 */
		public void incrementAmount(int amount) {
			if(this.isTimeout()) {
				this.amount = 0;
			}
			this.amount += amount;
			this.stopwatch.reset();
		}
		
		/**
		 * Determines if this counter has timed out or not.
		 * @return {@code true} if this counter has timed out, {@code false} otherwise.
		 */
		public boolean isTimeout() {
			return stopwatch.elapsed(CombatConstants.DAMAGE_CACHE_TIMEOUT, TimeUnit.SECONDS);
		}
	}
}
