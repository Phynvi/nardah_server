package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {2207})
public class Gowler extends SimplifiedListener<Mob> {

	private static MagicAttack MAGIC;

	static {
		try {
			MAGIC = new MagicAttack();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		return attacker.getStrategy().canAttack(attacker, defender);
	}

	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		attacker.setStrategy(MAGIC);
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(CombatProjectile.getDefinition("Fire Wave"));
		}

		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(7036, UpdatePriority.VERY_HIGH));
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 16, combatProjectile);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}
}
