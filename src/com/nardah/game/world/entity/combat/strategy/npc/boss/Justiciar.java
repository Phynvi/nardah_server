package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.pathfinding.path.SimplePathChecker;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.region.RegionManager;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.task.impl.ForceMovementTask;
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
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.ForceMovement;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.net.packet.out.SendMessage;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * Handles Justiciar Combat Strategy
 * @author Adam_#6723
 */
public class Justiciar extends MultiStrategy {
	private static Magic MAGIC = new Magic();
	private static Melee MELEE = new Melee();
	private static LightingRain LIGHTNING_RAIN = new LightingRain();
	private static TeleGrab TELE_GRAB = new TeleGrab();
	
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	private static final CombatStrategy<Mob>[] MAGIC_STRATEGIES = createStrategyArray(MAGIC, MAGIC, MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(MAGIC, MELEE, MELEE, MAGIC, MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	
	/**
	 * Constructs a new <code>Justiciar</code>.
	 */
	public Justiciar() {
		currentStrategy = MAGIC;
		currentStrategy = MELEE;
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
		
		if(!defender.getCombat().isAttacking()) {
			defender.animate(new Animation(7962, UpdatePriority.VERY_HIGH));
			defender.graphic(1196);
			defender.graphic(481);
			defender.speak("Night King, Lend me your powers for i am your faithful servant!");
			defender.prayer.deactivate(Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_RANGE);
			defender.getPlayer().send(new SendMessage("Your overhead prayers have been disabled!"));
			
			RegionManager.forNearbyPlayer(attacker, 20, other -> {
				if(RandomUtils.success(.65))
					return;
				
				World.schedule(2, () -> {
					Position destination = Utility.randomElement(defender.boundaries);
					World.sendGraphic(new Graphic(481), destination);
					other.move(destination);
					
				});
			});
		}
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
		}
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class Melee extends NpcRangedStrategy {
		
		public Melee() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			
			attacker.animate(new Animation(7962, UpdatePriority.VERY_HIGH));
			Projectile projectile = new Projectile(162, 50, 80, 85, 25);
			CombatHit hit = nextMeleeHit(attacker, defender, 21);
			defender.graphic(163);
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				projectile.send(attacker, other);
				World.schedule(2, () -> other.damage(nextMagicHit(attacker, other, 38)));
			});
			
			/*
			 * if (Utility.random(0, 8) == 1) { attacker.animate(new Animation(7965,
			 * UpdatePriority.VERY_HIGH)); attacker.graphic(new Graphic(1296,
			 * UpdatePriority.VERY_HIGH)); attacker.heal(50);
			 * attacker.speak("Time To HEAL!"); System.out.println("It executes!");
			 *
			 * }
			 */
			
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextRangedHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
			return roll + 50_000;
		}
		
	}
	
	/**
	 * Jisticiar magic strategy.
	 */
	private static class Magic extends NpcMagicStrategy {
		public Magic() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			Projectile projectile = new Projectile(393, 50, 80, 85, 25);
			attacker.animate(new Animation(7853, UpdatePriority.VERY_HIGH));
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				projectile.send(attacker, other);
				defender.graphic(157);
				World.schedule(2, () -> other.damage(nextMagicHit(attacker, other, 38)));
				
			});
			
			defender.prayer.deactivate(Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_RANGE);
			defender.getPlayer().send(new SendMessage("Your overhead prayers have been disabled!"));
			if(Utility.random(0, 10) == 1) {
				attacker.animate(new Animation(7965, UpdatePriority.VERY_HIGH));
				attacker.graphic(new Graphic(1296, UpdatePriority.VERY_HIGH));
				attacker.heal(130);
				attacker.speak("Time To HEAL!");
				
			}
			
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
			return roll + 50_000;
		}
	}
	
	private static class TeleGrab extends NpcMagicStrategy {
		TeleGrab() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			
			int disarmattack = 1;
			int disaramattackrandom = Utility.random(disarmattack, 5);
			if(disaramattackrandom == disarmattack) {
				attacker.animate(new Animation(7964, UpdatePriority.VERY_HIGH));
				Projectile projectile = new Projectile(1479, 50, 80, 85, 25);
				projectile.send(attacker, defender);
				defender.damage(new Hit(Utility.random(20, 50)));
				attacker.speak("I AM A DECENDENT OF HELL!");
			}
			int scheduleMove = 1;
			int moveplayers = Utility.random(scheduleMove, 7);
			if(scheduleMove == moveplayers) {
				RegionManager.forNearbyPlayer(attacker, 16, other -> World.schedule(1, () -> {
					Position destination = Utility.randomElement(attacker.boundaries);
					World.sendGraphic(new Graphic(190), destination);
					other.move(destination);
					other.message("Justiciar Moved you!.");
				}));
			}
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
	}
	
	private static class LightingRain extends NpcMagicStrategy {
		LightingRain() {
			super(CombatProjectile.getDefinition("Vorkath Frozen Special"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(7962, UpdatePriority.VERY_HIGH));
			World.sendProjectile(attacker, defender, new Projectile(395, 46, 80, 43, 31));
			World.schedule(1, () -> {
				if(defender.isPlayer()) {
					Position current = defender.getPosition();
					Position best = Utility.findBestInside(defender, attacker);
					int dx = current.getX() - best.getX();
					int dy = current.getY() - best.getY();
					
					Direction opposite = Direction.getFollowDirection(attacker.getPosition(), defender.getPosition());
					//                    for (int x = 1; x <= 2; x++) {
					int y = dy / (dx == 0 ? dy : dx);
					Position destination = current.transform(dx, y);
					if(SimplePathChecker.checkLine(defender, destination))
						current = destination;
					//                    }
					defender.damage(new Hit(Utility.random(1, 3)));
					defender.interact(attacker);
					defender.getPlayer().send(new SendMessage("Justiciar's sword throws you backwards."));
					
					Position offset = new Position(current.getX() - defender.getX(), current.getY() - defender.getY());
					ForceMovement movement = new ForceMovement(defender.getPosition(), offset, 33, 60, Direction.getOppositeDirection(opposite));
					
					int anim = defender.actorAnimation.getWalk();
					World.schedule(new ForceMovementTask(defender, 3, 0, movement, new Animation(3170, UpdatePriority.VERY_HIGH)) {
						@Override
						protected void onSchedule() {
							super.onSchedule();
							defender.actorAnimation.setWalk(3170);
							defender.locking.lock();
						}
						
						@Override
						protected void onCancel(boolean logout) {
							super.onCancel(logout);
							defender.actorAnimation.setWalk(anim);
							defender.locking.unlock();
						}
					});
				}
			});
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
	}
}
