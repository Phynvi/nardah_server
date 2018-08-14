package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import com.nardah.util.Utility;
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
 * @author Adam
 */
@NpcCombatListenerSignature(npcs = {465})
public class SkeletalWyvern extends SimplifiedListener<Mob> {
	private static DragonfireStrategy DRAGONFIRE;
	private static CombatStrategy<Mob>[] STRATEGIES;

	static {
		try {
			DRAGONFIRE = new DragonfireStrategy(CombatProjectile.getDefinition("Skeletal wyvern breathe"));
			STRATEGIES = createStrategyArray(new Melee(), DRAGONFIRE);
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
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 2;
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
