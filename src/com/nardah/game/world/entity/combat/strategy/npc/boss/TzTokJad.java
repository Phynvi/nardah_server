package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Daniel
 */
public class TzTokJad extends MultiStrategy {
	private static RangedAttack RANGED = new RangedAttack();
	private static MagicAttack MAGIC = new MagicAttack();
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(RANGED, MAGIC, NpcMeleeStrategy.get());
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(RANGED, MAGIC);
	
	public TzTokJad() {
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
	public void hit(Mob attacker, Actor defender, Hit hit) {
		super.hit(attacker, defender, hit);
		
		if(!defender.isPlayer())
			return;
		Player player = defender.getPlayer();
		
		if(currentStrategy.getCombatType().equals(CombatType.MELEE) && player.prayer.isActive(Prayer.PROTECT_FROM_MELEE)) {
			hit.setDamage(0);
		} else if(currentStrategy.getCombatType().equals(CombatType.RANGED) && player.prayer.isActive(Prayer.PROTECT_FROM_RANGE)) {
			hit.setDamage(0);
		} else if(currentStrategy.getCombatType().equals(CombatType.MAGIC) && player.prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
			hit.setDamage(0);
		}
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(NON_MELEE);
		}
	}
	
	@Override
	public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
		return roll + 150_000;
	}
	
	private static class RangedAttack extends NpcRangedStrategy {
		private final Animation ANIMATION = new Animation(2652, UpdatePriority.HIGH);
		
		RangedAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(ANIMATION);
			World.sendGraphic(new Graphic(451, UpdatePriority.HIGH), defender.getPosition());
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 97, 2, 2)};
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		private final Animation ANIMATION = new Animation(2656, UpdatePriority.HIGH);
		
		public MagicAttack() {
			super(CombatProjectile.getDefinition("Jad Magic"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 97, 5, 2);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
		
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
	}
}
