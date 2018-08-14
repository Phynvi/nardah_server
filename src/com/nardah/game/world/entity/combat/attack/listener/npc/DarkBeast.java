package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.util.RandomUtils;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

@NpcCombatListenerSignature(npcs = {4005})
public class DarkBeast extends SimplifiedListener<Mob> {
	private static MagicAttack MAGIC = new MagicAttack();

	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		if(RandomUtils.success(.85)) {
			attacker.setStrategy(NpcMeleeStrategy.get());
		} else {
			attacker.setStrategy(MAGIC);
		}
	}

	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(CombatProjectile.getDefinition("Fire Bolt"));
		}

		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(2730, UpdatePriority.VERY_HIGH));
			combatProjectile.sendProjectile(attacker, defender);

		}

		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 8, 2, 1);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}
}
