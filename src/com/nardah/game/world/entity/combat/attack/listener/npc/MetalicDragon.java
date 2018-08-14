package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Michael | Chex
 */
@NpcCombatListenerSignature(npcs = { /* Bronze */ 270, 271, /* Iron */ 272, 273, /* Steel */ 139, 274, 275,
		/* Mithril */ 2919})
public class MetalicDragon extends SimplifiedListener<Mob> {

	private static Dragonfire DRAGONFIRE;
	private static CombatStrategy<Mob>[] STRATEGIES;

	static {
		try {
			DRAGONFIRE = new Dragonfire();
			STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), DRAGONFIRE);
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
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(DRAGONFIRE);
		} else {
			attacker.setStrategy(randomStrategy(STRATEGIES));
		}
	}

	private static class Dragonfire extends DragonfireStrategy {
		private Dragonfire() {
			super(CombatProjectile.getDefinition("Metalic dragonfire"));
		}

		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 60, false)};
		}
	}

}
