package io.battlerune.game.world.entity.combat.strategy.npc.boss;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Daniel
 */
public class CorporealBeast extends MultiStrategy {
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final CrushMelee MELEE = new CrushMelee();
	private static final CombatStrategy<Npc>[] STRATEGIES = createStrategyArray(MAGIC, MELEE);
	
	public CorporealBeast() {
		currentStrategy = randomStrategy(STRATEGIES);
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void finishOutgoing(Npc attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		currentStrategy = randomStrategy(STRATEGIES);
	}
	
	@Override
	public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		//        if (hit.getDamage() > 32) {
		// 1/8 chance that the dark core will spawn
		// TODO: make all non-spear weapons have 50% damage reduction
		//        }
		defender.getCombat().attack(attacker);
		super.block(attacker, defender, hit, combatType);
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	@Override
	public int modifyAccuracy(Npc attacker, Actor defender, int roll) {
		return (int) (roll * 5.05);
	}
	
	private static final class CrushMelee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(1682, UpdatePriority.HIGH);
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 2;
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(1679, UpdatePriority.HIGH);
		
		private MagicAttack() {
			super(CombatProjectile.getDefinition("Corporeal Beast Magic"));
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			super.start(attacker, defender, hits);
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
			super.hit(attacker, defender, hit);
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, 65)};
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 10;
		}
	}
}
