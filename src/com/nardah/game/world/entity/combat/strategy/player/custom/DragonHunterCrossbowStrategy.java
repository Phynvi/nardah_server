package com.nardah.game.world.entity.combat.strategy.player.custom;

import com.nardah.game.world.entity.actor.mob.MobAssistant;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

public class DragonHunterCrossbowStrategy extends PlayerRangedStrategy {
	private static final DragonHunterCrossbowStrategy INSTANCE = new DragonHunterCrossbowStrategy();

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return increase10percent(defender, roll);
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int roll) {
		return increase10percent(defender, roll);
	}

	private static int increase10percent(Actor defender, int roll) {
		if(MobAssistant.isDragon(defender.id))
			return roll * 11 / 10;
		return roll;
	}

	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		if(attacker.equipment.contains(10519) || attacker.equipment.contains(10518)) {
			return 2;
		} else {
			return 3;

		}
	}

	public static DragonHunterCrossbowStrategy get() {
		return INSTANCE;
	}

}
