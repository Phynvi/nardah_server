package io.battlerune.game.world.entity.combat.attack.listener.npc;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
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
@NpcCombatListenerSignature(npcs = {2207})
public class Gowler extends SimplifiedListener<Npc> {

	private static MagicAttack MAGIC;

	static {
		try {
			MAGIC = new MagicAttack();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		return attacker.getStrategy().canAttack(attacker, defender);
	}

	@Override
	public void start(Npc attacker, Actor defender, Hit[] hits) {
		attacker.setStrategy(MAGIC);
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(getDefinition("Fire Wave"));
		}

		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(7036, UpdatePriority.VERY_HIGH));
		}

		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 16, combatProjectile);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}
}
