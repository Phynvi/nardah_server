package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;

import java.util.Deque;
import java.util.LinkedList;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;

/**
 * The combat definition for Mutant Tarn.
 * @author Adam_#6723
 */
public class MutantTarn extends MultiStrategy {
	private static final NpcMeleeStrategy MELEE = NpcMeleeStrategy.get();
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final RangedAttack RANGED = new RangedAttack();
	private static final FireballAttack FIREBALL = new FireballAttack();
	
	private static final DragonfireStrategy DRAGONFIRE = new DragonfireAttack();
	private static final DragonfireStrategy PINK_DRAGONFIRE = new PinkDragonfireAttack();
	private static final DragonfireStrategy VENOM_DRAGONFIRE = new VenomDragonfireAttack();
	
	private static final FrozenSpecial FROZEN_SPECIAL = new FrozenSpecial();
	
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(MELEE, DRAGONFIRE, MAGIC, FIREBALL, RANGED, VENOM_DRAGONFIRE, PINK_DRAGONFIRE);
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(FIREBALL, DRAGONFIRE, MAGIC, RANGED, VENOM_DRAGONFIRE, PINK_DRAGONFIRE);
	private final CombatStrategy<Mob>[] SPECIALS = createStrategyArray(FROZEN_SPECIAL);
	
	private final Deque<CombatStrategy<Mob>> strategyQueue = new LinkedList<>();
	private int specialIndex;
	
	public MutantTarn() {
		currentStrategy = MELEE;
	}
	
	@Override
	public void init(Mob attacker, Actor defender) {
		if(strategyQueue.isEmpty()) {
			for(int index = 0; index < 6; index++) {
				strategyQueue.add(RandomUtils.random(FULL_STRATEGIES));
			}
			strategyQueue.add(SPECIALS[specialIndex++ % SPECIALS.length]);
		}
		currentStrategy = strategyQueue.poll();
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(currentStrategy == MELEE && !MELEE.canAttack(attacker, defender)) {
			currentStrategy = RandomUtils.random(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public boolean withinDistance(Mob attacker, Actor defender) {
		if(currentStrategy == MELEE && !MELEE.withinDistance(attacker, defender)) {
			currentStrategy = RandomUtils.random(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	private static class RangedAttack extends NpcRangedStrategy {
		
		RangedAttack() {
			super(CombatProjectile.getDefinition("Mutant Tarn Ranged"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 32)};
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		
		MagicAttack() {
			super(CombatProjectile.getDefinition("Shadow Rush"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 32)};
		}
		
	}
	
	private static class FrozenSpecial extends NpcMagicStrategy {
		private final Projectile PROJECTILE = new Projectile(1470, 5, 85, 85, 25);
		
		FrozenSpecial() {
			super(CombatProjectile.getDefinition("Galvek Frozen Special"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			World.schedule(4, () -> {
				int randomHit = Utility.random(45, 75);
				Mob shadow = new Mob(1277, new Position(2524, 4655, 0)) {
					
					@Override
					public void appendDeath() {
						super.appendDeath();
						
					}
				};
				shadow.register();
				shadow.walkTo(defender, () -> {
					World.sendGraphic(new Graphic(1460, true), shadow.getPosition());
					defender.damage(new Hit(randomHit * shadow.getCurrentHealth() / shadow.getMaximumHealth()));
					defender.graphic(287);
					shadow.unregister();
				});
			});
			
		}
		
		@Override
		public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
			return 15;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, -1);
			combatHit.setAccurate(false);
			return new CombatHit[]{combatHit};
		}
	}
	
	private static class DragonfireAttack extends DragonfireStrategy {
		DragonfireAttack() {
			super(CombatProjectile.getDefinition("Mutant Tarn FireAttack"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 85, true)};
		}
	}
	
	private static class VenomDragonfireAttack extends DragonfireStrategy {
		VenomDragonfireAttack() {
			super(CombatProjectile.getDefinition("Mutant tarn randomAttack"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			super.hit(attacker, defender, hit);
			if(hit.isAccurate()) {
				defender.venom();
			}
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = CombatUtil.generateDragonfire(attacker, defender, 75, true);
			return new CombatHit[]{combatHit};
		}
	}
	
	private static class PinkDragonfireAttack extends DragonfireStrategy {
		PinkDragonfireAttack() {
			super(CombatProjectile.getDefinition("Galvek Pink Dragonfire"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public void hitsplat(Mob attacker, Actor defender, Hit hit) {
			super.hitsplat(attacker, defender, hit);
			
			if(defender.isPlayer()) {
				defender.prayer.reset();
			}
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 80, true)};
		}
	}
	
	private static class FireballAttack extends DragonfireStrategy {
		private final Projectile PROJECTILE = new Projectile(500, 5, 105, 85, 25);
		private final Graphic GRAPHIC = new Graphic(985, true);
		private Position position;
		
		FireballAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			super.start(attacker, defender, hits);
			position = defender.getPosition();
			
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
			PROJECTILE.send(attacker, position);
			
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			super.hitsplat(attacker, defender, hit);
			World.sendGraphic(GRAPHIC, position);
			hit.setAccurate(false);
			
			if(defender.getPosition().equals(position)) {
				hit.setAs(CombatUtil.generateDragonfire(attacker, defender, 150, true));
				hit.setAccurate(true);
			} else if(Utility.withinOctal(defender.getPosition(), defender.width(), defender.length(), position, 1, 1, 1)) {
				hit.setAs(CombatUtil.generateDragonfire(attacker, defender, 150, true));
				hit.modifyDamage(damage -> damage / 2);
				hit.setAccurate(true);
			}
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, -1, CombatUtil.getHitDelay(attacker, defender, CombatType.MAGIC) + 1, 1)};
		}
	}
	
}
