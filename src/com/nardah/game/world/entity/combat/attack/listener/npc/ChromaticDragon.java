package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Michael | Chex
 */
@NpcCombatListenerSignature(npcs = { /* Lava */ 6593, /* Green */ 260, 261, 262, 263, 264, /* Red */ 247, 248, 249, 250, 251, /* Blue */ 265, 4385, 5878, 5879, 5880, 5881, 5882, 267, /* Black */ 252, 253, 254, 255, 256, 257, 258, 259, 2642, 6500, 6501, 6502, 6636, 6652})
public class ChromaticDragon extends SimplifiedListener<Mob> {
	private static DragonfireStrategy DRAGONFIRE;
	private static CombatStrategy<Mob>[] STRATEGIES;

	static {
		try {
			DRAGONFIRE = new DragonfireStrategy(CombatProjectile.getDefinition("Chromatic dragonfire"));
			STRATEGIES = createStrategyArray(new CrushMelee(), new StabMelee(), DRAGONFIRE);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(DRAGONFIRE);
		}
		return attacker.getStrategy().canAttack(attacker, defender);
	}

	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(DRAGONFIRE);
		} else {
			attacker.setStrategy(randomStrategy(STRATEGIES));
		}
	}

	private static final class CrushMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(80, UpdatePriority.HIGH);

		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 1;
		}

		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}

	private static final class StabMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(91, UpdatePriority.HIGH);

		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 1;
		}

		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}

}
