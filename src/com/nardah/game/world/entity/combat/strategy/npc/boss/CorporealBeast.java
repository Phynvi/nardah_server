package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Daniel
 */
public class CorporealBeast extends MultiStrategy {
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final CrushMelee MELEE = new CrushMelee();
	private static final CombatStrategy<Mob>[] STRATEGIES = createStrategyArray(MAGIC, MELEE);
	
	public CorporealBeast() {
		currentStrategy = randomStrategy(STRATEGIES);
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		currentStrategy = randomStrategy(STRATEGIES);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		//        if (hit.getDamage() > 32) {
		// 1/8 chance that the dark core will spawn
		// TODO: make all non-spear weapons have 50% damage reduction
		//        }
		defender.getCombat().attack(attacker);
		super.block(attacker, defender, hit, combatType);
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	@Override
	public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
		return (int) (roll * 5.05);
	}
	
	private static final class CrushMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(1682, UpdatePriority.HIGH);
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 2;
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(1679, UpdatePriority.HIGH);
		
		private MagicAttack() {
			super(CombatProjectile.getDefinition("Corporeal Beast Magic"));
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			super.start(attacker, defender, hits);
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			super.hit(attacker, defender, hit);
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, 65)};
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
	}
}
