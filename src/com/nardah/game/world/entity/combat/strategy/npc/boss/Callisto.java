package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.pathfinding.path.SimplePathChecker;
import com.nardah.game.world.position.Position;
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
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.player.ForceMovement;
import com.nardah.net.packet.out.SendMessage;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * @author Daniel
 */
public class Callisto extends MultiStrategy {
	
	private static Roar ROAR = new Roar();
	private static Shockwave SHOCKWAVE = new Shockwave();
	private static Melee MELEE = new Melee();
	private static final CombatStrategy<Mob>[] FULL = createStrategyArray(MELEE, MELEE, MELEE, SHOCKWAVE);
	
	public Callisto() {
		currentStrategy = SHOCKWAVE;
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			if(RandomUtils.success(0.10)) {
				currentStrategy = ROAR;
			} else {
				currentStrategy = SHOCKWAVE;
			}
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
		if(RandomUtils.success(0.10)) {
			currentStrategy = ROAR;
		} else {
			currentStrategy = randomStrategy(FULL);
		}
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class Roar extends NpcMagicStrategy {
		Roar() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(4922, UpdatePriority.VERY_HIGH));
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
					defender.getPlayer().send(new SendMessage("Callisto's roar throws you backwards."));
					
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
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 3);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
	}
	
	private static class Shockwave extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(4921, UpdatePriority.HIGH);
		
		Shockwave() {
			super(CombatProjectile.getDefinition("Callisto Shockwave"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			super.hit(attacker, defender, hit);
			if(defender.isPlayer()) {
				defender.getPlayer().send(new SendMessage("Callisto's fury sends an almighty shockwave through you."));
				defender.graphic(new Graphic(80, true));
			}
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 23);
			hit.setAccurate(true);
			return new CombatHit[]{hit};
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
	}
	
	private static final class Melee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(4925, UpdatePriority.HIGH);
		
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
}
