package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.util.RandomUtils;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.task.TickableTask;
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
import com.nardah.game.world.entity.actor.data.LockType;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.entity.skill.Skill;

import static com.nardah.game.world.entity.combat.CombatUtil.*;

/**
 * @author Daniel
 */
public class Venenatis extends MultiStrategy {
	private static final PrayerDrain PRAYER_DRAIN = new PrayerDrain();
	private static final Magic MAGIC = new Magic();
	private static final Web WEB = new Web();
	
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), NpcMeleeStrategy.get(), MAGIC, MAGIC, PRAYER_DRAIN, WEB);
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(MAGIC, MAGIC, PRAYER_DRAIN, WEB);
	
	public Venenatis() {
		currentStrategy = MAGIC;
	}
	
	@Override
	public boolean withinDistance(Mob attacker, Actor defender) {
		if(currentStrategy == NpcMeleeStrategy.get() && !currentStrategy.withinDistance(attacker, defender) && !defender.prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.withinDistance(attacker, defender);
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.canAttack(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		//        if (hit.isAccurate() && !Area.inMulti(defender)) { TODO: Gotta make the area multi first
		//            hit.modifyDamage(damage -> RandomUtils.inclusive(1, 6));
		//        }
		
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		CombatStrategy<Mob> strategy = currentStrategy;
		currentStrategy.finishOutgoing(attacker, defender);
		
		if(strategy != currentStrategy)
			return;
		
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(NON_MELEE);
		} else {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		}
	}
	
	private static final class Magic extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(5322, UpdatePriority.HIGH);
		
		Magic() {
			super(CombatProjectile.getDefinition("Earth Wave"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender)};
		}
	}
	
	private static final class Web extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(5322, UpdatePriority.HIGH);
		private static final Graphic GRAPHIC = new Graphic(80, true, UpdatePriority.HIGH);
		
		Web() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public boolean canAttack(Mob attacker, Actor defender) {
			return !defender.locking.locked() && super.canAttack(attacker, defender);
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			super.hit(attacker, defender, hit);
			if(!hit.isAccurate())
				return;
			if(defender.isPlayer())
				defender.getPlayer().message("Venenatis hurls her web at you, sticking you to the ground.");
			defender.graphic(GRAPHIC);
			defender.locking.lock(5, LockType.WALKING);
		}
		
		@Override
		public void finishOutgoing(Mob attacker, Actor defender) {
			attacker.getCombat().setCooldown(0);
			((Venenatis) attacker.getStrategy()).currentStrategy = RandomUtils.randomExclude(FULL_STRATEGIES, WEB);
		}
		
		@Override
		public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
			return 0;
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender, 50)};
		}
	}
	
	private static final class PrayerDrain extends NpcMagicStrategy {
		PrayerDrain() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			if(!hit.isAccurate())
				return;
			World.schedule(new TickableTask(true, 1) {
				@Override
				protected void tick() {
					defender.graphic(new Graphic(172, true, UpdatePriority.HIGH));
					Skill prayer = defender.skills.get(Skill.PRAYER);
					prayer.modifyLevel(level -> level * 2 / 3);
					defender.skills.refresh(Skill.PRAYER);
					
					if(defender.isPlayer())
						defender.getPlayer().message("Your prayer has been drained!");
					
					if(tick == ((CombatHit) hit).getHitsplatDelay() - 1)
						cancel();
				}
			});
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			int hitDelay = getHitDelay(attacker, defender, CombatType.MAGIC);
			return new CombatHit[]{nextMagicHit(attacker, defender, 50, hitDelay, RandomUtils.inclusive(1, 3))};
		}
	}
	
}
