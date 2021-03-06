package com.nardah.game.world.entity.combat.strategy.player.custom;

import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * @author adameternal123
 */

public class ValyrianSwordStrategy extends PlayerMeleeStrategy {
	
	private static final ValyrianSwordStrategy INSTANCE = new ValyrianSwordStrategy();
	
	public String name() {
		return "Valyrian Sword";
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}
	
	/**
	 * Atack delay.
	 **/
	
	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return 2;
	}
	
	/**
	 * Instane's the class to be called upon,and applied to an item.
	 **/
	public static ValyrianSwordStrategy get() {
		return INSTANCE;
	}
	
}
