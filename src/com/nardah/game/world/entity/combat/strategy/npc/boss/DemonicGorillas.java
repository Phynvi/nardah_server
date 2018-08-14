package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.util.RandomUtils;
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
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.prayer.Prayer;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * Created by Daniel on 2017-11-29.
 */
public class DemonicGorillas extends MultiStrategy {
	private static final Melee MELEE = new Melee();
	private static final Magic MAGIC = new Magic();
	private static final Ranged RANGED = new Ranged();
	private static final CombatStrategy<Mob>[] STRATEGIES = createStrategyArray(MAGIC, MELEE, RANGED);
	private static final Integer[] FORMS = {7147, 7148, 7149};
	private int attackCount = 0;
	private int damageDealt = 0;
	private int form = -1;
	
	public DemonicGorillas() {
		this.currentStrategy = randomStrategy(STRATEGIES);
	}
	
	private CombatStrategy getNextStrategy() {
		return RandomUtils.randomExclude(STRATEGIES, currentStrategy);
	}
	
	private int getNextForm() {
		return RandomUtils.randomExclude(FORMS, form);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		int nextForm = -1;
		
		if(form == -1) {
			nextForm = defender.id;
		}
		
		damageDealt += hit.getDamage();
		if(damageDealt >= 50) {
			nextForm = getNextForm();
			damageDealt = 0;
			attackCount = 0;
		}
		
		if(nextForm > -1) {
			defender.transform(nextForm);
			form = nextForm;
			
			if(form == 7147 && !defender.prayer.isActive(Prayer.PROTECT_FROM_MELEE)) {
				defender.prayer.toggle(Prayer.PROTECT_FROM_MELEE);
			} else if(form == 7148 && !defender.prayer.isActive(Prayer.PROTECT_FROM_RANGE)) {
				defender.prayer.toggle(Prayer.PROTECT_FROM_RANGE);
			} else if(form == 7149 && !defender.prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
				defender.prayer.toggle(Prayer.PROTECT_FROM_MAGIC);
			}
		}
	}
	
	@Override
	public void hit(Mob attacker, Actor defender, Hit hit) {
		super.hit(attacker, defender, hit);
		if(hit.getDamage() == 0) {
			attackCount++;
			if(attackCount >= 3) {
				currentStrategy = getNextStrategy();
				attackCount = 0;
			}
		}
	}
	
	@Override
	public int modifyDefensive(Actor attacker, Mob defender, int roll) {
		return (int) (roll * 2.3);
	}
	
	private static final class Melee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(7226, UpdatePriority.HIGH);
		
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
	
	private static class Magic extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(7238, UpdatePriority.HIGH);
		
		private Magic() {
			super(CombatProjectile.getDefinition("Demonic Gorilla Magic"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, 31);
			combatHit.setAccurate(true);
			return new CombatHit[]{combatHit};
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
	
	private static class Ranged extends NpcRangedStrategy {
		private static final Animation ANIMATION = new Animation(7227, UpdatePriority.HIGH);
		
		private Ranged() {
			super(CombatProjectile.getDefinition("Demonic Gorilla Ranged"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 31)};
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
