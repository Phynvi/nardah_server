package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.npc.boss.galvek.GalvekUtility;
import com.nardah.game.world.entity.combat.strategy.npc.impl.DragonfireStrategy;
import com.nardah.game.world.object.CustomGameObject;
import com.nardah.game.world.pathfinding.TraversalMap;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.task.TickableTask;
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

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;

/**
 * The combat definition for Galvek.
 * @author Adam_#6723
 */
public class Galvek extends MultiStrategy {
	private static final NpcMeleeStrategy MELEE = NpcMeleeStrategy.get();
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final RangedAttack RANGED = new RangedAttack();
	private static final FireballAttack FIREBALL = new FireballAttack();
	
	private static final DragonfireStrategy DRAGONFIRE = new DragonfireAttack();
	private static final DragonfireStrategy PINK_DRAGONFIRE = new PinkDragonfireAttack();
	private static final DragonfireStrategy VENOM_DRAGONFIRE = new VenomDragonfireAttack();
	
	private static final VenomSpecial VENOM_SPECIAL = new VenomSpecial();
	private static final FrozenSpecial FROZEN_SPECIAL = new FrozenSpecial();
	
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(MELEE, DRAGONFIRE, MAGIC, FIREBALL, RANGED, VENOM_DRAGONFIRE, PINK_DRAGONFIRE);
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(FIREBALL, DRAGONFIRE, MAGIC, RANGED, VENOM_DRAGONFIRE, PINK_DRAGONFIRE);
	private final CombatStrategy<Mob>[] SPECIALS = createStrategyArray(VENOM_SPECIAL, FROZEN_SPECIAL);
	
	private final Deque<CombatStrategy<Mob>> strategyQueue = new LinkedList<>();
	private int specialIndex;
	
	public Galvek() {
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
	
	private static class VenomSpecial extends NpcMagicStrategy {
		VenomSpecial() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
			return 30;
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			World.schedule(2, () -> {
				attacker.animate(new Animation(7910, UpdatePriority.HIGH));
				List<Position> boundaries = TraversalMap.getTraversableTiles(attacker.getPosition().transform(-7, -7), 30, 30);
				Collections.shuffle(boundaries);
				
				World.schedule(1, () -> {
					
					Projectile projectile = new Projectile(1483, 10, 85, 85, 25);
					projectile.send(attacker, defender.getPosition());
					World.schedule(AcidTask(defender, defender.getPosition()));
					
					for(int index = 0; index < 40; index++) {
						Position position = boundaries.get(index);
						projectile.send(attacker, position);
						World.schedule(AcidTask(defender, position));
					}
					
					final Projectile projectile2 = new Projectile(1482, 15, 95, 85, 25);
					
					World.schedule(2, () -> World.schedule(DragonFire(attacker, defender, projectile2)));
				});
			});
			
		}
		
		private TickableTask DragonFire(Actor attacker, Actor defender, Projectile projectile) {
			return new TickableTask(false, 1) {
				
				@Override
				protected void tick() {
					if(attacker == null || attacker.isDead()) {
						cancel();
						return;
					}
					
					if(tick > 24) {
						cancel();
						return;
					}
					
					Position position = defender.getPosition().copy();
					projectile.send(attacker, position);
					
					World.schedule(3, () -> {
						World.sendGraphic(new Graphic(131, UpdatePriority.HIGH), position);
						
						if(defender.getPosition().equals(position)) {
							attacker.animate(new Animation(7904, UpdatePriority.HIGH));
							defender.writeDamage(new Hit(Utility.random(30)));
						}
					});
				}
			};
		}
		
		private TickableTask AcidTask(Actor defender, Position position) {
			return new TickableTask(false, 2) {
				private CustomGameObject object;
				
				@Override
				protected void tick() {
					if(tick == 1) { // replace this object with the one ethan sends.
						object = new CustomGameObject(32378, position);
						object.register();
					} else if(tick == 5) {
						object.unregister();
						cancel();
						return;
					}
					
					if(defender.getPosition().equals(position)) {
						defender.graphic(287);
						defender.writeDamage(new Hit(Utility.random(1, 50)));
					}
				}
			};
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, 0);
			combatHit.setAccurate(true);
			combatHit.setDamage(-1);
			return new CombatHit[]{combatHit};
		}
	}
	
	private static class RangedAttack extends NpcRangedStrategy {
		
		RangedAttack() {
			super(CombatProjectile.getDefinition("Galvek Ranged"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7904, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 32)};
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		
		MagicAttack() {
			super(CombatProjectile.getDefinition("Galvek Magic"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7901, UpdatePriority.HIGH);
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
			return new Animation(7901, UpdatePriority.HIGH);
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			if(Utility.random(1, 5) == 1) {
				attacker.animate(7909);
				attacker.transform(8096);
				attacker.animate(7908);
				attacker.getCombat().attack(defender);
				attacker.getCombat().isAttacking(defender);
				
				for(int x = 1; x < 10; x++) {
					System.out.println("[GALVEK] Transformation is good to go.");
				}
			}
			defender.graphic(new Graphic(369));
			// defender.locking.lock(LockType.FREEZE);
			
			// attacker.blockInteract = true;
			attacker.resetFace();
			attacker.face(new Position(2277, 4057));
			attacker.animate(new Animation(7904, UpdatePriority.HIGH));
			PROJECTILE.send(attacker, new Position(2277, 4057));
			// TODO Iterate it over the spawnData, and if the spawndata is equal to the
			// correct value from that enum, than edit the Position of the Tsunami NPC.
			
			// done
			
			GalvekUtility.SpawnData1 data = GalvekUtility.spawn;
			World.schedule(4, () -> {
				Mob tsunami = new Mob(8099, data.getTsunami()) {
					
					@Override
					public void appendDeath() {
						super.appendDeath();
						
					}
				};
				for(int x = 1; x < 10; x++) {
					System.out.println("SPAWNING TSUNAMI");
				}
				tsunami.register();
				tsunami.walkTo(defender, () -> {
					World.sendGraphic(new Graphic(1460, true), tsunami.getPosition());
					defender.damage(new Hit(60 * tsunami.getCurrentHealth() / tsunami.getMaximumHealth()));
					tsunami.unregister();
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
			super(CombatProjectile.getDefinition("Metalic Galvek dragonfire"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7904, UpdatePriority.HIGH);
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
			super(CombatProjectile.getDefinition("Galvek Venom Dragonfire"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7904, UpdatePriority.HIGH);
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
			// combatHit.setHitsplat(Hitsplat.VENOM);
			return new CombatHit[]{combatHit};
		}
	}
	
	private static class PinkDragonfireAttack extends DragonfireStrategy {
		PinkDragonfireAttack() {
			super(CombatProjectile.getDefinition("Galvek Pink Dragonfire"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7904, UpdatePriority.HIGH);
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
		private final Projectile PROJECTILE = new Projectile(1481, 5, 105, 85, 25);
		private final Graphic GRAPHIC = new Graphic(1466, true);
		private Position position;
		
		FireballAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7901, UpdatePriority.HIGH);
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
