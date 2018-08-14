package io.battlerune.game.world.entity.combat.strategy.player.custom;

import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.player.Player;

/**
 * @author Red
 */
public class LongbowStrategy extends PlayerRangedStrategy {
	private static final LongbowStrategy INSTANCE = new LongbowStrategy();

	private LongbowStrategy() {
	}

	@Override
	public int modifyDamage(Player attacker, Mob defender, int roll) {

		return (int) (roll * 1.6);
	}

	@Override
	public int getAttackDelay(Player attacker, Mob defender, FightType fightType) {
		return 5;
	}

	public static LongbowStrategy get() {
		return INSTANCE;
	}

}