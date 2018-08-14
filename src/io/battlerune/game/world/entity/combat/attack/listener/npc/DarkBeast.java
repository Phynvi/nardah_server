package io.battlerune.game.world.entity.combat.attack.listener.npc;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import io.battlerune.game.world.entity.combat.attack.listener.SimplifiedListener;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.util.RandomUtils;

import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

@NpcCombatListenerSignature(npcs = {4005})
public class DarkBeast extends SimplifiedListener<Npc> {
	private static MagicAttack MAGIC = new MagicAttack();

	@Override
	public void start(Npc attacker, Actor defender, Hit[] hits) {
		if(RandomUtils.success(.85)) {
			attacker.setStrategy(NpcMeleeStrategy.get());
		} else {
			attacker.setStrategy(MAGIC);
		}
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(getDefinition("Fire Bolt"));
		}

		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(2730, UpdatePriority.VERY_HIGH));
			combatProjectile.sendProjectile(attacker, defender);

		}

		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}

		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 8, 2, 1);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}
}
