package io.battlerune.game.world.entity.combat.strategy;

import io.battlerune.game.Animation;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.attack.FormulaFactory;
import io.battlerune.game.world.entity.combat.attack.listener.CombatListener;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.actor.Actor;

import static io.battlerune.game.world.entity.combat.CombatUtil.getHitDelay;
import static io.battlerune.game.world.entity.combat.CombatUtil.getHitsplatDelay;
import static io.battlerune.game.world.entity.combat.attack.FormulaFactory.getMaxHit;

public abstract class CombatStrategy<T extends Actor> implements CombatListener<T> {
	
	public abstract int getAttackDelay(T attacker, Actor defender, FightType fightType);
	
	public abstract int getAttackDistance(T attacker, FightType fightType);
	
	public abstract CombatHit[] getHits(T attacker, Actor defender);
	
	public abstract Animation getAttackAnimation(T attacker, Actor defender);
	
	@Override
	public abstract boolean canAttack(T attacker, Actor defender);
	
	@Override
	public boolean canOtherAttack(Actor attacker, T defender) {
		return true;
	}
	
	@Override
	public void start(T attacker, Actor defender, Hit[] hits) {
	}
	
	@Override
	public void attack(T attacker, Actor defender, Hit hit) {
	}
	
	@Override
	public void hit(T attacker, Actor defender, Hit hit) {
	}
	
	@Override
	public void block(Actor attacker, T defender, Hit hit, CombatType combatType) {
	}
	
	@Override
	public void preDeath(Actor attacker, T defender, Hit hit) {
	}
	
	@Override
	public void onDeath(Actor attacker, T defender, Hit hit) {
	}
	
	@Override
	public void preKill(T attacker, Actor defender, Hit hit) {
	}
	
	@Override
	public void onKill(T attacker, Actor defender, Hit hit) {
	}
	
	@Override
	public void hitsplat(T attacker, Actor defender, Hit hit) {
	}
	
	@Override
	public void finishOutgoing(T attacker, Actor defender) {
	}
	
	@Override
	public void finishIncoming(Actor attacker, T defender) {
	}
	
	public abstract CombatType getCombatType();
	
	/* ********** SIMPLIFIED ********** */
	
	protected CombatHit nextMeleeHit(T attacker, Actor defender) {
		int max = getMaxHit(attacker, defender, CombatType.MELEE);
		return nextMeleeHit(attacker, defender, max);
	}
	
	protected final CombatHit nextRangedHit(T attacker, Actor defender) {
		int max = getMaxHit(attacker, defender, CombatType.RANGED);
		return nextRangedHit(attacker, defender, max);
	}
	
	protected final CombatHit nextMagicHit(T attacker, Actor defender) {
		int max = getMaxHit(attacker, defender, CombatType.MAGIC);
		return nextMagicHit(attacker, defender, max);
	}
	
	/* ********** MAX HITS ********** */
	
	protected CombatHit nextMeleeHit(T attacker, Actor defender, int max) {
		int hitDelay = getHitDelay(attacker, defender, CombatType.MELEE);
		int hitsplatDelay = getHitsplatDelay(CombatType.MELEE);
		return nextMeleeHit(attacker, defender, max, hitDelay, hitsplatDelay);
	}
	
	protected final CombatHit nextRangedHit(T attacker, Actor defender, int max) {
		int hitDelay = getHitDelay(attacker, defender, CombatType.RANGED);
		int hitsplatDelay = getHitsplatDelay(CombatType.RANGED);
		return nextRangedHit(attacker, defender, max, hitDelay, hitsplatDelay);
	}
	
	protected final CombatHit nextMagicHit(T attacker, Actor defender, int max) {
		int hitDelay = getHitDelay(attacker, defender, CombatType.MAGIC);
		int hitsplatDelay = getHitsplatDelay(CombatType.MAGIC);
		return nextMagicHit(attacker, defender, max, hitDelay, hitsplatDelay);
	}
	
	/* ********** MAX HITS & COMBAT PROJECTILES ********** */
	
	protected final CombatHit nextRangedHit(T attacker, Actor defender, int max, CombatProjectile projectile) {
		int hitDelay = getHitDelay(attacker, defender, CombatType.RANGED);
		int hitsplatDelay = getHitsplatDelay(CombatType.RANGED);
		hitDelay = projectile.getHitDelay().orElse(hitDelay);
		hitsplatDelay = projectile.getHitsplatDelay().orElse(hitsplatDelay);
		return nextRangedHit(attacker, defender, max, hitDelay, hitsplatDelay);
	}
	
	protected CombatHit nextMagicHit(T attacker, Actor defender, int max, CombatProjectile projectile) {
		int hitDelay = getHitDelay(attacker, defender, CombatType.MAGIC);
		int hitsplatDelay = getHitsplatDelay(CombatType.MAGIC);
		hitDelay = projectile.getHitDelay().orElse(hitDelay);
		hitsplatDelay = projectile.getHitsplatDelay().orElse(hitsplatDelay);
		return nextMagicHit(attacker, defender, max, hitDelay, hitsplatDelay);
	}
	
	/* ********** COMBAT PROJECTILES ********** */
	
	protected final CombatHit nextRangedHit(T attacker, Actor defender, CombatProjectile projectile) {
		int max = getMaxHit(attacker, defender, CombatType.RANGED);
		return nextRangedHit(attacker, defender, max, projectile);
	}
	
	protected CombatHit nextMagicHit(T attacker, Actor defender, CombatProjectile projectile) {
		int max = projectile.getMaxHit();
		return nextMagicHit(attacker, defender, max, projectile);
	}
	
	/* ********** BASE METHODS ********** */
	
	protected CombatHit nextMeleeHit(T attacker, Actor defender, int max, int hitDelay, int hitsplatDelay) {
		return new CombatHit(FormulaFactory.nextMeleeHit(attacker, defender, max), hitDelay, hitsplatDelay);
	}
	
	protected CombatHit nextRangedHit(T attacker, Actor defender, int max, int hitDelay, int hitsplatDelay) {
		return new CombatHit(FormulaFactory.nextRangedHit(attacker, defender, max), hitDelay, hitsplatDelay);
	}
	
	protected final CombatHit nextMagicHit(T attacker, Actor defender, int max, int hitDelay, int hitsplatDelay) {
		return new CombatHit(FormulaFactory.nextMagicHit(attacker, defender, max), hitDelay, hitsplatDelay);
	}
	
}
