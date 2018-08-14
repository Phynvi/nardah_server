package io.battlerune.game.world.entity.combat.strategy.npc.boss;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.Projectile;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.CombatUtil;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.RandomUtils;
import io.battlerune.util.Utility;

import java.util.Deque;
import java.util.LinkedList;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;

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
	
	private static final CombatStrategy<Npc>[] FULL_STRATEGIES = createStrategyArray(MELEE, DRAGONFIRE, MAGIC, FIREBALL, RANGED, VENOM_DRAGONFIRE, PINK_DRAGONFIRE);
	private static final CombatStrategy<Npc>[] NON_MELEE = createStrategyArray(FIREBALL, DRAGONFIRE, MAGIC, RANGED, VENOM_DRAGONFIRE, PINK_DRAGONFIRE);
	private final CombatStrategy<Npc>[] SPECIALS = createStrategyArray(FROZEN_SPECIAL);
	
	private final Deque<CombatStrategy<Npc>> strategyQueue = new LinkedList<>();
	private int specialIndex;
	
	public MutantTarn() {
		currentStrategy = MELEE;
	}
	
	@Override
	public void init(Npc attacker, Mob defender) {
		if(strategyQueue.isEmpty()) {
			for(int index = 0; index < 6; index++) {
				strategyQueue.add(RandomUtils.random(FULL_STRATEGIES));
			}
			strategyQueue.add(SPECIALS[specialIndex++ % SPECIALS.length]);
		}
		currentStrategy = strategyQueue.poll();
	}
	
	@Override
	public boolean canAttack(Npc attacker, Mob defender) {
		if(currentStrategy == MELEE && !MELEE.canAttack(attacker, defender)) {
			currentStrategy = RandomUtils.random(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public boolean withinDistance(Npc attacker, Mob defender) {
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
		public Animation getAttackAnimation(Npc attacker, Mob defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 32)};
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		
		MagicAttack() {
			super(CombatProjectile.getDefinition("Shadow Rush"));
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Mob defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 32)};
		}
		
	}
	
	private static class FrozenSpecial extends NpcMagicStrategy {
		private final Projectile PROJECTILE = new Projectile(1470, 5, 85, 85, 25);
		
		FrozenSpecial() {
			super(CombatProjectile.getDefinition("Galvek Frozen Special"));
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Mob defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public void hit(Npc attacker, Mob defender, Hit hit) {
			World.schedule(4, () -> {
				int randomHit = Utility.random(45, 75);
				Npc shadow = new Npc(1277, new Position(2524, 4655, 0)) {
					
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
		public int getAttackDelay(Npc attacker, Mob defender, FightType fightType) {
			return 15;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
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
		public Animation getAttackAnimation(Npc attacker, Mob defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
			return new CombatHit[]{CombatUtil.generateDragonfire(attacker, defender, 85, true)};
		}
	}
	
	private static class VenomDragonfireAttack extends DragonfireStrategy {
		VenomDragonfireAttack() {
			super(CombatProjectile.getDefinition("Mutant tarn randomAttack"));
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Mob defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public void hit(Npc attacker, Mob defender, Hit hit) {
			super.hit(attacker, defender, hit);
			if(hit.isAccurate()) {
				defender.venom();
			}
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
			CombatHit combatHit = CombatUtil.generateDragonfire(attacker, defender, 75, true);
			return new CombatHit[]{combatHit};
		}
	}
	
	private static class PinkDragonfireAttack extends DragonfireStrategy {
		PinkDragonfireAttack() {
			super(CombatProjectile.getDefinition("Galvek Pink Dragonfire"));
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Mob defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public void hitsplat(Npc attacker, Mob defender, Hit hit) {
			super.hitsplat(attacker, defender, hit);
			
			if(defender.isPlayer()) {
				defender.prayer.reset();
			}
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
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
		public Animation getAttackAnimation(Npc attacker, Mob defender) {
			return new Animation(5617, UpdatePriority.HIGH);
		}
		
		@Override
		public void start(Npc attacker, Mob defender, Hit[] hits) {
			super.start(attacker, defender, hits);
			position = defender.getPosition();
			
		}
		
		@Override
		public void attack(Npc attacker, Mob defender, Hit hit) {
			PROJECTILE.send(attacker, position);
			
		}
		
		@Override
		public void hit(Npc attacker, Mob defender, Hit hit) {
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
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 10;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Mob defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, -1, CombatUtil.getHitDelay(attacker, defender, CombatType.MAGIC) + 1, 1)};
		}
	}
	
}
