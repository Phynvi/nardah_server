package io.battlerune.game.world.entity.combat.strategy.npc.boss;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.prayer.Prayer;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;
import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

/**
 * @author Daniel
 */
public class TzTokJad extends MultiStrategy {
	private static RangedAttack RANGED = new RangedAttack();
	private static MagicAttack MAGIC = new MagicAttack();
	private static final CombatStrategy<Npc>[] FULL_STRATEGIES = createStrategyArray(RANGED, MAGIC, NpcMeleeStrategy.get());
	private static final CombatStrategy<Npc>[] NON_MELEE = createStrategyArray(RANGED, MAGIC);
	
	public TzTokJad() {
		currentStrategy = randomStrategy(NON_MELEE);
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void hit(Npc attacker, Actor defender, Hit hit) {
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
	public void finishOutgoing(Npc attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(NON_MELEE);
		}
	}
	
	@Override
	public int modifyAccuracy(Npc attacker, Actor defender, int roll) {
		return roll + 150_000;
	}
	
	private static class RangedAttack extends NpcRangedStrategy {
		private final Animation ANIMATION = new Animation(2652, UpdatePriority.HIGH);
		
		RangedAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			attacker.animate(ANIMATION);
			World.sendGraphic(new Graphic(451, UpdatePriority.HIGH), defender.getPosition());
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 97, 2, 2)};
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		private final Animation ANIMATION = new Animation(2656, UpdatePriority.HIGH);
		
		public MagicAttack() {
			super(getDefinition("Jad Magic"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 97, 5, 2);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
		
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}
	}
}
