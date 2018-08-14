package io.battlerune.game.world.entity.actor.npc;

import io.battlerune.game.task.impl.ForceChatEvent;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.Combat;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.listener.CombatListener;
import io.battlerune.game.world.entity.combat.attack.listener.CombatListenerManager;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.*;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.arena.Arena;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl.FlightKilisa;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl.FlockleaderGeerin;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl.KreeArra;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl.WingmanSkree;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.dagannoths.DagannothPrime;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.dagannoths.DagannothRex;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.dagannoths.DagannothSupreme;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.dagannoths.Spinolyp;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.kril.Balfrug;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.kril.Tstanon;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.kril.Zakln;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.scorpia.Scorpia;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.scorpia.ScorpiaGuardian;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.scorpia.ScorpiaOffspring;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.skotizo.Skotizo;
import io.battlerune.game.world.entity.actor.npc.definition.NpcDefinition;
import io.battlerune.util.parser.impl.NpcForceChatParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Method handles small methods for npcs that do not have any parent class.
 * @author Daniel
 */
public class NpcAssistant {
	
	public final int[] BOSSES = {239, /* King Black Dragon */
			7039, /* Blurite Dragon */
			6618, /* Crazy Archeologist */
			319, /* Corporeal Beast */
			6619, /* Chaos Fanatic */
			2042, /* Zulrah */
			2043, /* Zulrah */
			2044, /* Zulrah */
			3127, /* Tz-tok Jad */
			6609, /* Callisto */
			2267, /* Dagannoth Rex */
			2266, /* Dagannoth Prime */
			2265, /* Dagganoth Supreme */
			6615, /* Scorpia */
			6611, /* Vet'ion */
			7286, /* Skotizo */
			5129, 6504, /* Venenatis */
			3162, /* Kree'arra */};
	
	public static final Map<Integer, Supplier<CombatStrategy<Npc>>> STRATEGIES = new HashMap<Integer, Supplier<CombatStrategy<Npc>>>() {
		{
			put(239, KingBlackDragonStrategy::new);
			put(7039, BluriteDragonStrategy::new);
			put(6618, CrazyArchaeologist::new);
			put(319, CorporealBeast::new);
			put(6619, ChaosFanatic::new);
			put(2042, Zulrah::new);
			put(3127, TzTokJad::new);
			put(6609, Callisto::new);
			put(7147, DemonicGorillas::new);
			put(7148, DemonicGorillas::new);
			put(7149, DemonicGorillas::new);
			put(2267, DagannothRex::new);
			put(2266, DagannothPrime::new);
			put(2265, DagannothSupreme::new);
			put(5947, Spinolyp::new);
			put(6615, Scorpia::new);
			put(6616, ScorpiaOffspring::new);
			put(6617, ScorpiaGuardian::new);
			put(6611, Vetion::new);
			put(7286, Skotizo::new);
			put(5129, Arena::new);
			put(763, GiantRoc::new);
			put(6504, Venenatis::new);
			put(3130, Tstanon::new);
			put(5862, Cerberus::new);
			put(3131, Zakln::new);
			put(6477, MutantTarn::new);
			put(3132, Balfrug::new);
			put(3162, KreeArra::new);
			put(8060, Vorkath::new);
			put(3163, WingmanSkree::new);
			put(3164, FlockleaderGeerin::new);
			put(3165, FlightKilisa::new);
			put(8095, Galvek::new);
			put(8096, Galvek::new);
			put(7858, Justiciar::new);
			put(7860, Porazdir::new);
			put(7859, Derwen::new);
			put(7585, IceDemon::new);
			put(6766, LizardShaman::new);
			put(6767, LizardShaman::new);
		}
	};
	
	private static final int[] DRAGONS = { /* Bronze */ 270, 271, /* Iron */ 272, 273, /* Steel */ 139, 274, 275,
			/* Mithril */ 2919,
			
			/* Green */ 260, 261, 262, 263, 264, /* Red */ 247, 248, 249, 250, 251, /* Blue */ 265, 4385, 5878, 5879, 5880, 5881, 5882, /* Black */ 239, 252, 253, 254, 255, 256, 257, 258, 259, 2642, 6500, 6501, 6502, 6636, 6652};
	
	/**
	 * The npc.
	 */
	private Npc npc;
	
	/**
	 * Creates a new <code>NpcAssistant<code>
	 */
	NpcAssistant(Npc npc) {
		this.npc = npc;
	}
	
	/**
	 * Handles initializing all the npc assistant methods on login.
	 */
	public void login() {
		Combat<Npc> combat = npc.getCombat();
		NpcDefinition definition = NpcDefinition.get(npc.id);
		npc.definition = definition;
		
		npc.setWidth(definition.getSize());
		npc.setLength(definition.getSize());
		npc.setBonuses(definition.getBonuses());
		npc.actorAnimation.setNpcAnimations(definition);
		reloadSkills();
		
		CombatListener<Npc> listener = CombatListenerManager.NPC_LISTENERS.get(npc.id);
		if(listener != null) {
			combat.addListener(listener);
		}
		
		npc.setStrategy(STRATEGIES.getOrDefault(npc.id, () -> loadStrategy(npc).orElse(NpcMeleeStrategy.get())).get());
		setEvent();
	}
	
	private void reloadSkills() {
		for(int index = 0; index < npc.definition.getSkills().length; index++) {
			npc.skills.setNpcMaxLevel(index, npc.definition.getSkills()[index]);
		}
	}
	
	public static boolean isDragon(int id) {
		for(int dragon : DRAGONS)
			if(id == dragon)
				return true;
		return false;
	}
	
	private static Optional<CombatStrategy<Npc>> loadStrategy(Npc npc) {
		if(!npc.definition.getCombatAttackData().isPresent()) {
			return Optional.empty();
		}
		
		NpcDefinition.CombatAttackData data = npc.definition.getCombatAttackData().get();
		CombatType type = data.type;
		
		switch(type) {
			case RANGED: {
				CombatProjectile definition = data.getDefinition();
				if(definition == null) {
					throw new AssertionError("Could not find ranged projectile for Actor[id=" + npc.id + ", name=" + npc.getName() + "]");
				}
				return Optional.of(new NpcRangedStrategy(definition));
			}
			case MAGIC: {
				CombatProjectile definition = data.getDefinition();
				if(definition == null) {
					throw new AssertionError("Could not find magic projectile for Actor[id=" + npc.id + ", name=" + npc.getName() + "]");
				}
				return Optional.of(new NpcMagicStrategy(definition));
			}
			case MELEE:
				return Optional.of(NpcMeleeStrategy.get());
		}
		return Optional.empty();
	}
	
	/**
	 * Handles the npc respawning.
	 */
	void respawn() {
		npc.setVisible(true);
		npc.skills.restoreAll();
		npc.getCombat().reset();
	}
	
	/**
	 * Handles setting all the events for the npc.
	 */
	private void setEvent() {
		if(NpcForceChatParser.FORCED_MESSAGES.containsKey(npc.spawnPosition)) {
			NpcForceChatParser.ForcedMessage forcedMessage = NpcForceChatParser.FORCED_MESSAGES.get(npc.spawnPosition);
			if(forcedMessage.getId() == npc.id) {
				World.schedule(new ForceChatEvent(npc, forcedMessage));
			}
		}
	}
}
