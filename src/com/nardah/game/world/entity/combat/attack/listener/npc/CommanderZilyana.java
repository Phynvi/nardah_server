package com.nardah.game.world.entity.combat.attack.listener.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
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
@NpcCombatListenerSignature(npcs = {2205})
public class CommanderZilyana extends SimplifiedListener<Mob> {

	private static MagicAttack MAGIC;
	private static CombatStrategy<Mob>[] STRATEGIES;

	private static final String[] SHOUTS = {"Death to the enemies of the light!", "Slay the evil ones!", "Saradomin lend me strength!", "By the power of Saradomin!", "May Saradomin be my sword!", "Good will always triumph!", "Forward! Our allies are with us!", "Saradomin is with us!", "In the name of Saradomin!", "All praise Saradomin!", "Attack! Find the Godsword!"};

	static {
		try {
			MAGIC = new MagicAttack();
			STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), MAGIC);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(MAGIC);
		}
		return attacker.getStrategy().canAttack(attacker, defender);
	}

	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			attacker.setStrategy(MAGIC);
		} else {
			attacker.setStrategy(randomStrategy(STRATEGIES));
		}
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
		private MagicAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}

		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(6970, UpdatePriority.VERY_HIGH));
		}

		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}

		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 31);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}
}
