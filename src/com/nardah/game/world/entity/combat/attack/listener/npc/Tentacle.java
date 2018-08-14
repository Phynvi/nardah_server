package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {5535})
public class Tentacle extends SimplifiedListener<Mob> {

	private static MagicAttack MAGIC;

	static {
		try {
			MAGIC = new MagicAttack();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
		return roll + 25_000;
	}

	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		attacker.setStrategy(MAGIC);
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(CombatProjectile.getDefinition("Tentacle Blast"));
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, 2);
			combatHit.setAccurate(true);
			return new CombatHit[]{combatHit};
		}
	}
}
