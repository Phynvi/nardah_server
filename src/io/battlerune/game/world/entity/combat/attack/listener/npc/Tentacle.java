package io.battlerune.game.world.entity.combat.attack.listener.npc;

import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {5535})
public class Tentacle extends SimplifiedListener<Npc> {

	private static MagicAttack MAGIC;

	static {
		try {
			MAGIC = new MagicAttack();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int modifyAccuracy(Npc attacker, Actor defender, int roll) {
		return roll + 25_000;
	}

	@Override
	public void start(Npc attacker, Actor defender, Hit[] hits) {
		attacker.setStrategy(MAGIC);
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(getDefinition("Tentacle Blast"));
		}

		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, 2);
			combatHit.setAccurate(true);
			return new CombatHit[]{combatHit};
		}
	}
}
