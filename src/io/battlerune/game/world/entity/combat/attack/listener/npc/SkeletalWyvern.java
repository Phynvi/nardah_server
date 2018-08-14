package io.battlerune.game.world.entity.combat.attack.listener.npc;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.util.Utility;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;
import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

/**
 * @author Adam
 */
@NpcCombatListenerSignature(npcs = {465})
public class SkeletalWyvern extends SimplifiedListener<Npc> {
	private static DragonfireStrategy DRAGONFIRE;
	private static CombatStrategy<Npc>[] STRATEGIES;

	static {
		try {
			DRAGONFIRE = new DragonfireStrategy(getDefinition("Skeletal wyvern breathe"));
			STRATEGIES = createStrategyArray(new Melee(), DRAGONFIRE);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(DRAGONFIRE);
		}
		return attacker.getStrategy().canAttack(attacker, defender);
	}

	@Override
	public void finishOutgoing(Npc attacker, Actor defender) {

		int standardAttack = 1;
		int standardAttackRandom = Utility.random(standardAttack, 5);

		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(DRAGONFIRE);
		} else {
			attacker.setStrategy(randomStrategy(STRATEGIES));
		}
		if(standardAttackRandom == 1) {
			defender.animate(2989);
		}
	}

	private static final class Melee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(2988, UpdatePriority.HIGH);
		private static final Animation ANIMATION_1 = new Animation(2989, UpdatePriority.HIGH);

		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 2;
		}

		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}

		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
}
