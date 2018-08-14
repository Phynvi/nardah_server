package com.nardah.game.world.entity.combat.strategy.player.custom;

import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

/**
 * @author Adam_#6723
 */
public class TwistedBowStrategy extends PlayerRangedStrategy {
	private static final TwistedBowStrategy INSTANCE = new TwistedBowStrategy();
	
	private TwistedBowStrategy() {
	}
	
	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		int level = defender.skills.getMaxLevel(Skill.MAGIC);
		if(level > 360)
			level = 360;
		int a = (3 * level) / 10 - 100;
		int mod = 140 + (3 * level - 10) / 100 - (a * a) / 100;
		if(mod > 140)
			mod = 140;
		return roll * mod / 100;
	}
	
	@Override
	public int modifyDamage(Player attacker, Actor defender, int roll) {
		int level = defender.skills.getMaxLevel(Skill.MAGIC);
		if(level > 360)
			level = 360;
		int a = (3 * level) / 10 - 140;
		int mod = 250 + (3 * level - 14) / 100 - (a * a) / 100;
		if(mod > 250)
			mod = 250;
		return roll * mod / 100;
	}
	
	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return 6;
	}
	
	public static TwistedBowStrategy get() {
		return INSTANCE;
	}
	
}