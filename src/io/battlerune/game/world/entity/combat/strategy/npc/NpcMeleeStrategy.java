package io.battlerune.game.world.entity.combat.strategy.npc;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.effect.impl.CombatPoisonEffect;
import io.battlerune.game.world.entity.combat.effect.impl.CombatVenomEffect;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.basic.MeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.util.RandomUtils;

public class NpcMeleeStrategy extends MeleeStrategy<Npc> {
	
	private static final NpcMeleeStrategy INSTANCE = new NpcMeleeStrategy();
	
	protected NpcMeleeStrategy() {
	}
	
	@Override
	public void start(Npc attacker, Actor defender, Hit[] hits) {
		attacker.animate(getAttackAnimation(attacker, defender));
	}
	
	@Override
	public void attack(Npc attacker, Actor defender, Hit hit) {
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
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	@Override
	public int getAttackDistance(Npc attacker, FightType fightType) {
		return 1;
	}
	
	@Override
	public CombatHit[] getHits(Npc attacker, Actor defender) {
		return new CombatHit[]{nextMeleeHit(attacker, defender)};
	}
	
	@Override
	public Animation getAttackAnimation(Npc attacker, Actor defender) {
		return new Animation(attacker.definition.getAttackAnimation(), UpdatePriority.HIGH);
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
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
