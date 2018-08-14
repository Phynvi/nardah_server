package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {1672})
public class Ahrim extends SimplifiedListener<Mob> {

	private static FireBlast FIRE_BLAST = new FireBlast();
	private static Confuse CONFUSE = new Confuse();
	private static Weaken WEAKEN = new Weaken();
	private static Curse CURSE = new Curse();
	private static final CombatStrategy<Mob>[] STRATEGIES = createStrategyArray(FIRE_BLAST, FIRE_BLAST, FIRE_BLAST, CONFUSE, WEAKEN, CURSE);

	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		attacker.setStrategy(randomStrategy(STRATEGIES));
	}

	private static class FireBlast extends NpcMagicStrategy {
		private FireBlast() {
			super(CombatProjectile.getDefinition("Fire Blast"));
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 25);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}

	private static class Confuse extends NpcMagicStrategy {
		private Confuse() {
			super(CombatProjectile.getDefinition("Confuse"));
		}
	}

	private static class Weaken extends NpcMagicStrategy {
		private Weaken() {
			super(CombatProjectile.getDefinition("Weaken"));
		}
	}

	private static class Curse extends NpcMagicStrategy {
		private Curse() {
			super(CombatProjectile.getDefinition("Curse"));
		}
	}
}
