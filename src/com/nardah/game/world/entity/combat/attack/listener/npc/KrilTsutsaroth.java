package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.listener.NpcCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Daniel
 */
@NpcCombatListenerSignature(npcs = {3129})
public class KrilTsutsaroth extends SimplifiedListener<Mob> {
	private static CombatStrategy<Mob>[] STRATEGIES;
	private static final String[] SHOUTS = {"Attack them, you dogs!", "Attack!", "YARRRRRRRR!", "Rend them limb from limb!", "Forward!", "No retreat!",};

	static {
		try {
			STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), new MagicAttack());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		attacker.setStrategy(randomStrategy(STRATEGIES));
		if(Utility.random(3) == 0) {
			attacker.speak(Utility.randomElement(SHOUTS));
		}
	}

	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		attacker.getStrategy().block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}

	private static class MagicAttack extends NpcMagicStrategy {
		public MagicAttack() {
			super(CombatProjectile.getDefinition("Kril Tsutsaroth"));
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 30);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}
}
