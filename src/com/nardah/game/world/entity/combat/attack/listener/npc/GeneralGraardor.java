package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.position.Area;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {2215})
public class GeneralGraardor extends SimplifiedListener<Mob> {
	private static MeleeAttack MELEE;
	private static RangedAttack RANGED;

	private static final String[] SHOUTS = {"Brargh!", "Split their skulls!", "All glory to Bandos!", "GRRRAAAAAR!", "CHAAARGE!", "Crush them underfoot!", "Death to our enemies!", "Break their bones!", "For the glory of the Big High War God!", "We feast on the bones of our enemies tonight!"};

	static {
		try {
			MELEE = new MeleeAttack();
			RANGED = new RangedAttack();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		if(attacker.getStrategy().equals(NpcMeleeStrategy.get())) {
			attacker.setStrategy(MELEE);
		}

		if(RandomUtils.success(0.35)) {
			attacker.speak(Utility.randomElement(SHOUTS));
		}
	}

	private static class MeleeAttack extends NpcMeleeStrategy {
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7018, UpdatePriority.HIGH);
		}

		@Override
		public void finishOutgoing(Mob attacker, Actor defender) {
			if(RandomUtils.success(0.40)) {
				attacker.setStrategy(RANGED);
			}
		}
	}

	private static class RangedAttack extends NpcRangedStrategy {
		RangedAttack() {
			super(CombatProjectile.getDefinition("Graardor Ranged"));
		}

		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7021, UpdatePriority.HIGH);
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 35)};
		}

		@Override
		public void hitsplat(Mob attacker, Actor defender, Hit hit) {
			super.hitsplat(attacker, defender, hit);

			CombatUtil.areaAction(attacker, 64, 18, mob -> {
				if(mob.isPlayer() && Area.inGraardor(mob)) {
					mob.damage(nextRangedHit(attacker, defender, 35));
				}
			});
		}

		@Override
		public void finishOutgoing(Mob attacker, Actor defender) {
			if(attacker.getStrategy() != MELEE) {
				attacker.setStrategy(MELEE);
			}
		}

		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 32;
		}

	}

}
