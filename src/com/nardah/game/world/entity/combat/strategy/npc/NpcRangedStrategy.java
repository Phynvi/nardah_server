package com.nardah.game.world.entity.combat.strategy.npc;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.entity.combat.strategy.basic.RangedStrategy;
import com.nardah.util.RandomUtils;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatImpact;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.attack.FormulaFactory;
import com.nardah.game.world.entity.combat.effect.impl.CombatPoisonEffect;
import com.nardah.game.world.entity.combat.effect.impl.CombatVenomEffect;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class NpcRangedStrategy extends RangedStrategy<Mob> {
	
	private final CombatProjectile projectileDefinition;
	
	public NpcRangedStrategy(CombatProjectile projectileDefinition) {
		this.projectileDefinition = projectileDefinition;
	}
	
	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		Animation animation = getAttackAnimation(attacker, defender);
		
		if(projectileDefinition.getAnimation().isPresent() && (animation.getId() == -1 || animation.getId() == 65535)) {
			animation = projectileDefinition.getAnimation().get();
		}
		
		attacker.animate(animation);
		projectileDefinition.getStart().ifPresent(attacker::graphic);
		projectileDefinition.sendProjectile(attacker, defender);
	}
	
	@Override
	public void attack(Mob attacker, Actor defender, Hit hit) {
		Predicate<CombatImpact> filter = effect -> effect.canAffect(attacker, defender, hit);
		Consumer<CombatImpact> execute = effect -> effect.impact(attacker, defender, hit, null);
		projectileDefinition.getEffect().filter(filter).ifPresent(execute);
		
		if(!attacker.definition.isPoisonous()) {
			return;
		}
		
		if(CombatVenomEffect.isVenomous(attacker) && RandomUtils.success(0.25)) {
			defender.venom();
		} else {
			CombatPoisonEffect.getPoisonType(attacker.id).ifPresent(defender::poison);
		}
	}
	
	@Override
	public void hit(Mob attacker, Actor defender, Hit hit) {
		projectileDefinition.getEnd().ifPresent(defender::graphic);
	}
	
	@Override
	public CombatHit[] getHits(Mob attacker, Actor defender) {
		int max = projectileDefinition.getMaxHit();
		if(max == -1)
			max = FormulaFactory.getMaxHit(attacker, defender, getCombatType());
		return new CombatHit[]{nextRangedHit(attacker, defender, max, projectileDefinition)};
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	@Override
	public int getAttackDistance(Mob attacker, FightType fightType) {
		return 10;
	}
	
	@Override
	public Animation getAttackAnimation(Mob attacker, Actor defender) {
		return new Animation(attacker.definition.getAttackAnimation(), UpdatePriority.HIGH);
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		return true;
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.RANGED;
	}
	
}
