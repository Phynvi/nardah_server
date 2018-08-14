package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Daniel
 */
public class CrazyArchaeologist extends MultiStrategy {
	private static RainAttack RAIN = new RainAttack();
	private static RangeAttack RANGE = new RangeAttack();
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(RAIN, RANGE, NpcMeleeStrategy.get(), RANGE, NpcMeleeStrategy.get());
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(RAIN, RANGE, RANGE, RANGE, RANGE);
	
	private static final String[] MESSAGES = {"I'm Bellock - respect me!", "Get off my site!", "No-one messes with Bellock's dig!", "These ruins are mine!", "Taste my knowledge!", "You belong in a museum!",};
	
	public CrazyArchaeologist() {
		currentStrategy = randomStrategy(NON_MELEE);
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		if(currentStrategy != RAIN) {
			attacker.speak(Utility.randomElement(MESSAGES));
		}
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class RainAttack extends NpcMagicStrategy {
		
		public RainAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(1162, UpdatePriority.VERY_HIGH));
			attacker.speak("Rain of knowledge!");
			for(int i = 0; i < 3; i++) {
				int offsetX = defender.getX() - attacker.getX();
				int offsetY = defender.getY() - attacker.getY();
				if(i == 0 || i == 2) {
					offsetX += i == 0 ? -1 : 1;
					offsetY++;
				}
				World.sendProjectile(new Projectile(1260, 46, 80, 43, 31), attacker.getPosition(), -1, (byte) offsetX, (byte) offsetY);
				Position end = new Position(attacker.getX() + offsetX, attacker.getY() + offsetY, 0);
				World.schedule(3, () -> {
					if(defender.getPosition().equals(end)) {
						defender.damage(nextMagicHit(attacker, defender, 23, combatProjectile));
					}
					World.sendGraphic(new Graphic(131, UpdatePriority.HIGH), end);
				});
			}
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 23, combatProjectile);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
			return roll + 50_000;
		}
		
	}
	
	private static class RangeAttack extends NpcRangedStrategy {
		public RangeAttack() {
			super(CombatProjectile.getDefinition("Archaeologist Range"));
		}
	}
}
