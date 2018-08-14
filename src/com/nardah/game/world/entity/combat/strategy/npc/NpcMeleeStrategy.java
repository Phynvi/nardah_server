package com.nardah.game.world.entity.combat.strategy.npc;

import com.nardah.game.world.entity.combat.strategy.basic.MeleeStrategy;
import com.nardah.util.RandomUtils;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.effect.impl.CombatPoisonEffect;
import com.nardah.game.world.entity.combat.effect.impl.CombatVenomEffect;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;

public class NpcMeleeStrategy extends MeleeStrategy<Mob> {
	
	private static final NpcMeleeStrategy INSTANCE = new NpcMeleeStrategy();
	
	protected NpcMeleeStrategy() {
	}
	
	@Override
	public void start(Mob attacker, Actor defender, Hit[] hits) {
		attacker.animate(getAttackAnimation(attacker, defender));
	}
	
	@Override
	public void attack(Mob attacker, Actor defender, Hit hit) {
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
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	@Override
	public int getAttackDistance(Mob attacker, FightType fightType) {
		return 1;
	}
	
	@Override
	public CombatHit[] getHits(Mob attacker, Actor defender) {
		return new CombatHit[]{nextMeleeHit(attacker, defender)};
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
		return CombatType.MELEE;
	}
	
	public static NpcMeleeStrategy get() {
		return INSTANCE;
	}
	
}
