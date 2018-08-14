package com.nardah.game.world.entity.combat.attack.listener;

import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.util.*;

/**
 * @author StanTheWoman
 */
public final class CombatListenerManager {

	private static Logger logger = LogManager.getLogger(LoggerType.START_UP);
	private static final Map<Integer, Set<CombatListenerSet>> ITEM_LISTENERS = new HashMap<>();
	public static final Map<Integer, CombatListener<Mob>> NPC_LISTENERS = new HashMap<>();

	public static void load() {
		loadItems();
		loadNpcs();
	}

	private static void loadItems() {
		new FastClasspathScanner().matchClassesWithAnnotation(ItemCombatListenerSignature.class, clazz -> {
			try {
				ItemCombatListenerSignature meta = clazz.getAnnotation(ItemCombatListenerSignature.class);
				CombatListener<Player> listener = (CombatListener<Player>) clazz.getConstructor().newInstance();

				for(int item : meta.items()) {
					Set<CombatListenerSet> set = ITEM_LISTENERS.getOrDefault(item, new HashSet<>());
					set.add(new CombatListenerSet(meta.items(), meta.requireAll(), listener));
					ITEM_LISTENERS.put(item, set);
				}
			} catch(Exception ex) {
				logger.error(String.format("Error loading item set combat listener=%s", clazz.getSimpleName()));
				ex.printStackTrace();
			}
		}).scan();
		logger.info(String.format("Loaded: %d item set combat listeners.", ITEM_LISTENERS.size()));
	}

	private static void loadNpcs() {
		new FastClasspathScanner().matchClassesWithAnnotation(NpcCombatListenerSignature.class, clazz -> {
			try {
				NpcCombatListenerSignature meta = clazz.getAnnotation(NpcCombatListenerSignature.class);
				CombatListener<Mob> listener = (CombatListener<Mob>) clazz.getConstructor().newInstance();

				for(int npc : meta.npcs()) {
					NPC_LISTENERS.put(npc, listener);
				}
			} catch(Exception ex) {
				logger.error(String.format("Error loading mob combat listener=%s", clazz.getSimpleName()));
				ex.printStackTrace();
			}
		}).scan();
		logger.info(String.format("Loaded: %d mob combat listeners.", NPC_LISTENERS.size()));
	}

	public static void addListener(Player player, int id) {
		Set<CombatListenerSet> sets = ITEM_LISTENERS.get(id);

		if(sets == null) {
			return;
		}

		for(CombatListenerSet set : sets) {
			if(set.requireAll && !player.equipment.containsAll(set.set)) {
				continue;
			}

			if(!set.requireAll && !player.equipment.containsAny(set.set)) {
				continue;
			}

			player.getCombat().addListener(set.listener);
			// System.out.println("Adding listener " +
			// set.listener.getClass().getSimpleName() + " to " + player.getName());
		}
	}

	public static void removeListener(Player player, int id) {
		Set<CombatListenerSet> sets = ITEM_LISTENERS.get(id);

		if(sets == null) {
			return;
		}

		for(CombatListenerSet set : sets) {
			if(set.requireAll && player.equipment.containsAll(set.set)) {
				continue;
			}

			if(!set.requireAll && player.equipment.containsAny(set.set)) {
				continue;
			}

			player.getCombat().removeListener(set.listener);
			// System.out.println("Removing listener " +
			// set.listener.getClass().getSimpleName() + " from " + player.getName());
		}
	}

	public static void removeAllPlayerListeners(Player player) {
		for(Set<CombatListenerSet> i : ITEM_LISTENERS.values()) {
			if(i == null) {
				return;
			}

			for(CombatListenerSet set : i) {
				if(set.requireAll && player.equipment.containsAll(set.set)) {
					continue;
				}

				if(!set.requireAll && player.equipment.containsAny(set.set)) {
					continue;
				}

				player.getCombat().removeListener(set.listener);
				// System.out.println("(RemoveAll) Removing listener " +
				// set.listener.getClass().getSimpleName() + " from " + player.getName());
			}
		}
	}

	public static final class CombatListenerSet {
		private final int[] set;
		private final boolean requireAll;
		private final CombatListener<Player> listener;

		CombatListenerSet(int[] set, boolean requireAll, CombatListener<Player> listener) {
			this.set = set;
			this.requireAll = requireAll;
			this.listener = listener;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == this)
				return true;
			if(obj instanceof CombatListenerSet) {
				CombatListenerSet other = (CombatListenerSet) obj;
				return Arrays.equals(other.set, set) && other.requireAll == requireAll;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(set, requireAll);
		}

		@Override
		public String toString() {
			return "ItemSet[set=" + Arrays.toString(set) + ", requireAll=" + requireAll + "]";
		}
	}
}