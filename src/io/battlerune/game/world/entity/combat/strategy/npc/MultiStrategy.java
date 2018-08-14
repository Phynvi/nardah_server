package io.battlerune.game.world.entity.combat.strategy.npc;

import io.battlerune.game.Animation;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

public abstract class MultiStrategy extends CombatStrategy<Npc> {
	protected CombatStrategy<Npc> currentStrategy;
	
	@Override
	public boolean withinDistance(Npc attacker, Actor defender) {
		return currentStrategy.withinDistance(attacker, defender);
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public boolean canOtherAttack(Actor attacker, Npc defender) {
		return currentStrategy.canOtherAttack(attacker, defender);
	}
	
	@Override
	public void start(Npc attacker, Actor defender, Hit[] hits) {
		currentStrategy.start(attacker, defender, hits);
	}
	
	@Override
	public void attack(Npc attacker, Actor defender, Hit hit) {
		currentStrategy.attack(attacker, defender, hit);
	}
	
	@Override
	public void hit(Npc attacker, Actor defender, Hit hit) {
		currentStrategy.hit(attacker, defender, hit);
	}
	
	@Override
	public void hitsplat(Npc attacker, Actor defender, Hit hit) {
		currentStrategy.hitsplat(attacker, defender, hit);
	}
	
	@Override
	public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
	}
	
	@Override
	public void preDeath(Actor attacker, Npc defender, Hit hit) {
		currentStrategy.preDeath(attacker, defender, hit);
	}
	
	@Override
	public void onDeath(Actor attacker, Npc defender, Hit hit) {
		currentStrategy.onDeath(attacker, defender, hit);
	}
	
	@Override
	public void preKill(Npc attacker, Actor defender, Hit hit) {
		currentStrategy.preKill(attacker, defender, hit);
	}
	
	@Override
	public void onKill(Npc attacker, Actor defender, Hit hit) {
		currentStrategy.onKill(attacker, defender, hit);
	}
	
	@Override
	public void finishIncoming(Actor attacker, Npc defender) {
		currentStrategy.finishIncoming(attacker, defender);
	}
	
	@Override
	public void finishOutgoing(Npc attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return currentStrategy.getAttackDelay(attacker, defender, fightType);
	}
	
	@Override
	public int getAttackDistance(Npc attacker, FightType fightType) {
		return currentStrategy.getAttackDistance(attacker, fightType);
	}
	
	@Override
	public CombatHit[] getHits(Npc attacker, Actor defender) {
		return currentStrategy.getHits(attacker, defender);
	}
	
	@Override
	public Animation getAttackAnimation(Npc attacker, Actor defender) {
		return currentStrategy.getAttackAnimation(attacker, defender);
	}
	
	@Override
	public CombatType getCombatType() {
		return currentStrategy.getCombatType();
	}
	
	@Override
	public int modifyAccuracy(Npc attacker, Actor defender, int roll) {
		return currentStrategy.modifyAccuracy(attacker, defender, roll);
	}
	
	@Override
	public int modifyAggressive(Npc attacker, Actor defender, int roll) {
		return currentStrategy.modifyAggressive(attacker, defender, roll);
	}
	
	@Override
	public int modifyDefensive(Actor attacker, Npc defender, int roll) {
		return currentStrategy.modifyDefensive(attacker, defender, roll);
	}
	
	@Override
	public int modifyDamage(Npc attacker, Actor defender, int damage) {
		return currentStrategy.modifyDamage(attacker, defender, damage);
	}
	
	@Override
	public int modifyAttackLevel(Npc attacker, Actor defender, int level) {
		return currentStrategy.modifyAttackLevel(attacker, defender, level);
	}
	
	@Override
	public int modifyStrengthLevel(Npc attacker, Actor defender, int level) {
		return currentStrategy.modifyStrengthLevel(attacker, defender, level);
	}
	
	@Override
	public int modifyDefenceLevel(Actor attacker, Npc defender, int level) {
		return currentStrategy.modifyDefenceLevel(attacker, defender, level);
	}
	
	@Override
	public int modifyRangedLevel(Npc attacker, Actor defender, int level) {
		return currentStrategy.modifyRangedLevel(attacker, defender, level);
	}
	
	@Override
	public int modifyMagicLevel(Npc attacker, Actor defender, int level) {
		return currentStrategy.modifyMagicLevel(attacker, defender, level);
	}
	
	@Override
	public int modifyOffensiveBonus(Npc attacker, Actor defender, int bonus) {
		return currentStrategy.modifyOffensiveBonus(attacker, defender, bonus);
	}
	
	@Override
	public int modifyAggressiveBonus(Npc attacker, Actor defender, int bonus) {
		return currentStrategy.modifyAggressiveBonus(attacker, defender, bonus);
	}
	
	@Override
	public int modifyDefensiveBonus(Actor attacker, Npc defender, int bonus) {
		return currentStrategy.modifyDefensiveBonus(attacker, defender, bonus);
	}
	
}
