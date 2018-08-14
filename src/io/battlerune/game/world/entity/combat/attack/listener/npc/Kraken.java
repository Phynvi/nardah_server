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
@NpcCombatListenerSignature(npcs = {494})
public class Kraken extends SimplifiedListener<Npc> {

	private static MagicAttack MAGIC = new MagicAttack();

	@Override
	public int modifyAccuracy(Npc attacker, Actor defender, int roll) {
		return roll + 25000;
	}

	@Override
	public int modifyAggressive(Npc attacker, Actor defender, int roll) {
		return roll + 15000;
	}

	@Override
	public void start(Npc attacker, Actor defender, Hit[] hits) {
		attacker.setStrategy(MAGIC);
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(getDefinition("Kraken Blast"));
		}

		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, combatProjectile.getMaxHit());
			combatHit.setAccurate(true);
			return new CombatHit[]{combatHit};
		}
	}
}
